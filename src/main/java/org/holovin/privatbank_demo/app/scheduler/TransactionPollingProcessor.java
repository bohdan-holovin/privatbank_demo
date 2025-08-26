package org.holovin.privatbank_demo.app.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.service.TransactionProcessor;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.domain.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionPollingProcessor {

    private static final int workerCount = 5;
    private static final int batchSize = 5;
    private static final int pollingDelayMs = 2000;

    private final TransactionRepository transactionRepository;
    private final TransactionProcessor transactionProcessor;
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    private ScheduledExecutorService scheduledExecutor;

    @PostConstruct
    public void startWorkers() {
        scheduledExecutor = Executors.newScheduledThreadPool(
                workerCount,
                runnable -> new Thread(runnable, "TransactionWorker-" + System.currentTimeMillis())
        );

        IntStream.range(0, workerCount)
                .forEach(i -> {
                    var workerId = i + 1;
                    var initialDelay = i * 500;
                    scheduledExecutor.scheduleWithFixedDelay(
                            () -> workerLoop(workerId),
                            initialDelay,
                            pollingDelayMs,
                            TimeUnit.MILLISECONDS
                    );
                });

        log.info("Started {} transaction workers with {}ms polling delay", workerCount, pollingDelayMs);
    }

    private void workerLoop(int workerId) {
        if (isShuttingDown.get()) {
            return;
        }

        var threadName = Thread.currentThread().getName();
        log.info("Worker-{} ({}): search", workerId, threadName);

        try {
            var transactions = transactionRepository.findPendingTransactions(batchSize);

            if (!transactions.isEmpty()) {
                log.info("Worker-{} ({}): claimed {} transactions", workerId, threadName, transactions.size());

                var processed = 0;
                var failed = 0;

                for (Transaction transaction : transactions) {
                    if (isShuttingDown.get()) {
                        log.info("Worker-{}: shutdown requested, stopping processing", workerId);
                        break;
                    }

                    try {
                        transactionProcessor.processTransaction(transaction);
                        processed++;
                        log.debug("Worker-{}: processed transaction {} {}", workerId, transaction.getId(), transaction.getUuid());
                    } catch (Exception e) {
                        failed++;
                        log.error(
                                "Worker-{}: failed to process transaction {} {}: {}",
                                workerId, transaction.getId(), transaction.getUuid(), e.getMessage(), e
                        );
                    }
                }

                log.info("Worker-{}: batch completed - {} processed, {} failed", workerId, processed, failed);
            }

        } catch (Exception e) {
            log.error("Worker-{}: error in worker loop: {}", workerId, e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down transaction workers...");
        isShuttingDown.set(true);

        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Workers didn't shutdown gracefully, forcing shutdown");
                    scheduledExecutor.shutdownNow();

                    if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.error("Could not terminate workers");
                    }
                }
                log.info("Transaction workers shut down successfully");
            } catch (InterruptedException e) {
                log.warn("Shutdown interrupted, forcing immediate shutdown");
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}