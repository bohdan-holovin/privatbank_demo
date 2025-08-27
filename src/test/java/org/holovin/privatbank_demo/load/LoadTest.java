package org.holovin.privatbank_demo.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.request.account.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.account.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.account.AccountWithdrawRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.user.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
class LoadTest {

    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 500;
    private static final int SLEEP_TIME_MILLIS = 60000;

    private final List<UserResponseDto> users = new CopyOnWriteArrayList<>();
    private final AtomicInteger successCounter = new AtomicInteger(0);
    private final AtomicInteger errorCounter = new AtomicInteger(0);

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void stressTestTransactions() throws Exception {
        log.info("===== STARTING LOAD TEST =====");
        log.info("Concurrent users: {}, requests per user: {}", CONCURRENT_USERS, REQUESTS_PER_USER);

        var userExecutor = Executors.newFixedThreadPool(10);
        IntStream.range(0, CONCURRENT_USERS)
                .forEach(i -> userExecutor.submit(() -> createUser(i)));
        shutdownAndAwait(userExecutor, "user creation");

        assertThat(users).isNotEmpty();
        log.info("Created {} users", users.size());

        var txExecutor = Executors.newFixedThreadPool(20);
        IntStream.range(0, CONCURRENT_USERS)
                .forEach(i -> txExecutor.submit(() -> performUserTransactions(i)));
        shutdownAndAwait(txExecutor, "transactions");

        Thread.sleep(SLEEP_TIME_MILLIS);

        printResults();
        var transactionStats = printTransactionStatistics();

        assertThat(successCounter.get()).isEqualTo(transactionStats.totalTransactions);
    }

    private void shutdownAndAwait(ExecutorService executor, String stageName) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
            log.error("Timeout while waiting for {}", stageName);
            executor.shutdownNow();
        }
    }

    private void createUser(int userIndex) {
        try {
            var request = new UserRegistrationRequestDto();
            request.setUsername("user_" + UUID.randomUUID());
            request.setAccountCount(2);

            var result = mockMvc.perform(post("/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andReturn();

            if (result.getResponse().getStatus() == 200) {
                var user = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
                users.add(user);
            } else {
                log.warn("User creation failed [{}]: {}", userIndex, result.getResponse().getStatus());
            }

        } catch (Exception e) {
            log.error("Error creating user {}: {}", userIndex, e.getMessage());
        }
    }

    private void performUserTransactions(int userIndex) {
        if (users.isEmpty()) return;

        IntStream.range(0, REQUESTS_PER_USER).forEach(i -> {
            try {
                int operation = ThreadLocalRandom.current().nextInt(3);
                switch (operation) {
                    case 0 -> performTopUpTransaction();
                    case 1 -> performWithdrawTransaction();
                    case 2 -> performTransferTransaction();
                }
            } catch (Exception e) {
                errorCounter.incrementAndGet();
                log.debug("Transaction failed for user {}: {}", userIndex, e.getMessage());
            }
        });
    }

    private void performTopUpTransaction() throws Exception {
        var user = getRandomUser();
        var request = new AccountTopUpRequestDto(
                UUID.randomUUID().toString(),
                user.getAccounts().getFirst().getAccountNumber(),
                BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(100, 1000))
        );

        var result = mockMvc.perform(post("/accounts/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) successCounter.incrementAndGet();
        else errorCounter.incrementAndGet();
    }

    private void performWithdrawTransaction() throws Exception {
        var user = getRandomUser();
        var request = new AccountWithdrawRequestDto(
                UUID.randomUUID().toString(),
                user.getAccounts().getFirst().getAccountNumber(),
                BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(100, 1000))
        );

        var result = mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) successCounter.incrementAndGet();
        else errorCounter.incrementAndGet();
    }

    private void performTransferTransaction() throws Exception {
        if (users.size() < 2) return;

        var fromUser = getRandomUser();
        var toUser = getRandomUser();

        if (Objects.equals(fromUser.getAccounts().getFirst().getId(), toUser.getAccounts().getFirst().getId())) return;

        var request = new AccountTransferRequestDto(
                UUID.randomUUID().toString(),
                fromUser.getAccounts().getFirst().getAccountNumber(),
                toUser.getAccounts().getFirst().getAccountNumber(),
                BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(100, 1000))
        );

        var result = mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) successCounter.incrementAndGet();
        else errorCounter.incrementAndGet();
    }

    private UserResponseDto getRandomUser() {
        return users.get(ThreadLocalRandom.current().nextInt(users.size()));
    }

    private void printResults() {
        int totalRequests = successCounter.get() + errorCounter.get();
        log.info("\n\n\n");
        log.info("===== LOAD TEST RESULTS =====");
        log.info("Total number of requests: {}", totalRequests);
        log.info("Successful requests: {}", successCounter.get());
        log.info("Failed requests: {}", errorCounter.get());
        log.info("Active users: {}", users.size());
        log.info("=====================================");
    }

    private TransactionStats printTransactionStatistics() {
        var stats = getTransactionStats();

        log.info("===== TRANSACTION STATISTICS IN DB =====");
        log.info("Transactions created during test: {}", stats.totalTransactions);
        log.info("PENDING: {} ({}%)", stats.pending,
                String.format("%.2f", calculatePercentage(stats.pending, stats.totalTransactions)));
        log.info("PROCESSING: {} ({}%)", stats.processing,
                String.format("%.2f", calculatePercentage(stats.processing, stats.totalTransactions)));
        log.info("COMPLETED: {} ({}%)", stats.completed,
                String.format("%.2f", calculatePercentage(stats.completed, stats.totalTransactions)));
        log.info("FAILED: {} ({}%)", stats.failed,
                String.format("%.2f", calculatePercentage(stats.failed, stats.totalTransactions)));

        log.info("===== STATISTICS BY TRANSACTION TYPE =====");
        log.info("TOP_UP: {}", stats.topUp);
        log.info("WITHDRAW: {}", stats.withdraw);
        log.info("TRANSFER: {}", stats.transfer);

        log.info("===== FAILURE REASONS =====");
        if (stats.failed > 0) {
            printFailureReasons();
        } else {
            log.info("No failed transactions!");
        }

        log.info("=========================================\n\n\n");

        return stats;
    }

    private TransactionStats getTransactionStats() {
        var total = (Long) entityManager
                .createQuery("SELECT COUNT(t) FROM Transaction t")
                .getSingleResult();

        var statusResults = entityManager
                .createQuery("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status")
                .getResultList();

        var typeResults = entityManager
                .createQuery("SELECT t.type, COUNT(t) FROM Transaction t GROUP BY t.type")
                .getResultList();

        var stats = new TransactionStats();
        stats.totalTransactions = total;

        for (Object[] row : (List<Object[]>) statusResults) {
            var status = (Transaction.Status) row[0];
            var count = (Long) row[1];
            switch (status) {
                case COMPLETED -> stats.completed = count;
                case FAILED -> stats.failed = count;
                case PENDING -> stats.pending = count;
                case PROCESSING -> stats.processing = count;
            }
        }

        for (Object[] result : (List<Object[]>) typeResults) {
            var type = (Transaction.Type) result[0];
            var count = (Long) result[1];

            switch (type) {
                case TOP_UP -> stats.topUp = count;
                case WITHDRAW -> stats.withdraw = count;
                case TRANSFER -> stats.transfer = count;
            }
        }

        return stats;
    }

    private void printFailureReasons() {
        var failureQuery = entityManager
                .createQuery("SELECT t.failureReason, COUNT(t) FROM Transaction t WHERE t.status = 'FAILED' GROUP BY t.failureReason")
                .getResultList();

        for (Object[] result : (List<Object[]>) failureQuery) {
            var reason = (String) result[0];
            var count = (Long) result[1];
            log.info("{}: {} times", reason != null ? reason : "Unknown reason", count);
        }
    }

    private double calculatePercentage(long part, long total) {
        return total > 0 ? (part * 100.0 / total) : 0.0;
    }

    private static class TransactionStats {
        long totalTransactions;
        long completed;
        long failed;
        long pending;
        long processing;
        long topUp;
        long withdraw;
        long transfer;
    }
}
