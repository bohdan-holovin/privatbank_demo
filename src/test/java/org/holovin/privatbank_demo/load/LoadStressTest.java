package org.holovin.privatbank_demo.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.AccountWithdrawRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class LoadStressTest {

    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 20;
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
    public void loadTest() throws InterruptedException {
        log.info("Starting aggressive stress test:");
        log.info("Concurrent users: {}", CONCURRENT_USERS);
        log.info("Requests per user: {}", REQUESTS_PER_USER);
        log.info("Total number of requests: {}", CONCURRENT_USERS * REQUESTS_PER_USER);

        var userCreationTasks = IntStream.range(0, CONCURRENT_USERS)
                .mapToObj(i -> CompletableFuture.runAsync(() -> createUser(i)))
                .toList();

        CompletableFuture.allOf(userCreationTasks.toArray(new CompletableFuture[0])).join();

        log.info("Created users {}", users.size());

        var transactionTasks = IntStream.range(0, CONCURRENT_USERS)
                .mapToObj(i -> CompletableFuture.runAsync(() -> performUserTransactions(i)))
                .toList();

        CompletableFuture.allOf(transactionTasks.toArray(new CompletableFuture[0])).join();

        Thread.sleep(45000);

        printResults();
        printTransactionStatistics(getTransactionCount());
    }

    private void createUser(int userIndex) {
        try {
            var request = new UserRegistrationRequestDto();
            request.setUsername(UUID.randomUUID().toString());
            request.setAccountCount(2);

            var result = mockMvc.perform(post("/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andReturn();

            if (result.getResponse().getStatus() == 200) {
                var responseBody = result.getResponse().getContentAsString();
                var user = objectMapper.readValue(responseBody, UserResponseDto.class);
                users.add(user);
            } else {
                log.warn("User creation failed for {}: {}", userIndex, result.getResponse().getStatus());
            }

        } catch (Exception e) {
            log.error("Error creating user {}: {}", userIndex, e.getMessage());
        }
    }

    private void performUserTransactions(int userIndex) {
        if (users.isEmpty()) {
            log.warn("No available users for transactions");
            return;
        }

        for (int i = 0; i < REQUESTS_PER_USER; i++) {
            try {
                int operation = ThreadLocalRandom.current().nextInt(3);

                switch (operation) {
                    case 0 -> performTopUpTransaction();
                    case 1 -> performWithdrawTransaction();
                    case 2 -> performTransferTransaction();
                }

            } catch (Exception e) {
                errorCounter.incrementAndGet();
                log.debug("User transaction failed for {}: {}", userIndex, e.getMessage());
            }
        }
    }

    private void performTopUpTransaction() throws Exception {
        var user = getRandomUser();
        if (user == null) return;

        var request = new AccountTopUpRequestDto();
        request.setAccountNumber(user.getAccounts().getFirst().getAccountNumber());
        request.setAmount(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(100, 1000)));
        request.setUuid(UUID.randomUUID().toString());

        var result = mockMvc.perform(post("/accounts/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            successCounter.incrementAndGet();
        } else {
            errorCounter.incrementAndGet();
        }
    }

    private void performWithdrawTransaction() throws Exception {
        var user = getRandomUser();
        if (user == null) return;

        var request = new AccountWithdrawRequestDto();
        request.setAccountNumber(user.getAccounts().getFirst().getAccountNumber());
        request.setAmount(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(100, 1000)));
        request.setUuid(UUID.randomUUID().toString());

        var result = mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            successCounter.incrementAndGet();
        } else {
            errorCounter.incrementAndGet();
        }
    }

    private void performTransferTransaction() throws Exception {
        if (users.size() < 2) return;

        var fromUser = getRandomUser();
        var toUser = getRandomUser();

        if (fromUser == null || toUser == null || fromUser.getAccounts().getFirst().getId().equals(toUser.getAccounts().getFirst().getId())) {
            return;
        }

        var request = new AccountTransferRequestDto();
        request.setFromAccountNumber(fromUser.getAccounts().getFirst().getAccountNumber());
        request.setToAccountNumber(toUser.getAccounts().getFirst().getAccountNumber());
        request.setAmount(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(100, 1000)));
        request.setUuid(UUID.randomUUID().toString());

        var result = mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            successCounter.incrementAndGet();
        } else {
            errorCounter.incrementAndGet();
        }
    }

    private UserResponseDto getRandomUser() {
        if (users.isEmpty()) return null;
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

    private void printTransactionStatistics(long initialCount) {
        try {
            TransactionStats stats = getTransactionStats();
            long totalCreatedTransactions = stats.totalTransactions - initialCount;

            log.info("===== TRANSACTION STATISTICS IN DB =====");
            log.info("Transactions created during test: {}", totalCreatedTransactions);
            log.info("PENDING: {} ({}%)", stats.pending,
                    String.format("%.2f", calculatePercentage(stats.pending, totalCreatedTransactions)));
            log.info("PROCESSING: {} ({}%)", stats.processing,
                    String.format("%.2f", calculatePercentage(stats.processing, totalCreatedTransactions)));
            log.info("COMPLETED: {} ({}%)", stats.completed,
                    String.format("%.2f", calculatePercentage(stats.completed, totalCreatedTransactions)));
            log.info("FAILED: {} ({}%)", stats.failed,
                    String.format("%.2f", calculatePercentage(stats.failed, totalCreatedTransactions)));

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

        } catch (Exception e) {
            log.error("Error while fetching transaction statistics: {}", e.getMessage());
        }
    }

    private long getTransactionCount() {
        var query = entityManager.createQuery("SELECT COUNT(t) FROM Transaction t");
        return (Long) query.getSingleResult();
    }

    private TransactionStats getTransactionStats() {
        var totalQuery = entityManager.createQuery("SELECT COUNT(t) FROM Transaction t");
        long total = (Long) totalQuery.getSingleResult();

        var statusQuery = entityManager.createQuery("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status");
        var statusResults = statusQuery.getResultList();

        var typeQuery = entityManager.createQuery("SELECT t.type, COUNT(t) FROM Transaction t GROUP BY t.type");
        var typeResults = typeQuery.getResultList();

        var stats = new TransactionStats();
        stats.totalTransactions = total;

        for (Object[] result : (List<Object[]>) statusResults) {
            var status = (Transaction.Status) result[0];
            var count = (Long) result[1];

            switch (status) {
                case PENDING -> stats.pending = count;
                case PROCESSING -> stats.processing = count;
                case COMPLETED -> stats.completed = count;
                case FAILED -> stats.failed = count;
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
        var failureQuery = entityManager.createQuery("SELECT t.failureReason, COUNT(t) FROM Transaction t WHERE t.status = 'FAILED' GROUP BY t.failureReason");
        var results = failureQuery.getResultList();

        for (Object[] result : (List<Object[]>) results) {
            var reason = (String) result[0];
            var count = (Long) result[1];
            log.info("{}: {} times", reason != null ? reason : "Unknown reason", count);
        }
    }

    private double calculatePercentage(long part, long total) {
        return total > 0 ? (part * 100.0 / total) : 0.0;
    }

    private static class TransactionStats {
        long totalTransactions = 0;
        long pending = 0;
        long processing = 0;
        long completed = 0;
        long failed = 0;
        long topUp = 0;
        long withdraw = 0;
        long transfer = 0;
    }
}
