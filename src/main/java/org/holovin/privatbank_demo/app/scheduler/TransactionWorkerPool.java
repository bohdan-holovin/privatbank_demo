package org.holovin.privatbank_demo.app.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.service.TransactionService;
import org.holovin.privatbank_demo.app.usecase.worker.ProcessTransactionUseCase;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionWorkerPool {

    private static final int WORKER_COUNT = 5;
    private static final int BATCH_SIZE = 10;
    private static final int POLLING_DELAY_MS = 1000;
    private static final int EMPTY_BATCH_DELAY_MS = 2000;

    private final TransactionService transactionService;
    private final ProcessTransactionUseCase processTransactionUseCase;
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private final List<Future<?>> workerFutures = new ArrayList<>();
    private ExecutorService executor;

    @PostConstruct
    public void startWorkers() {
        executor = Executors.newFixedThreadPool(
                WORKER_COUNT,
                runnable -> {
                    var thread = new Thread(runnable, "TransactionWorker-" + System.currentTimeMillis());
                    thread.setDaemon(false);
                    return thread;
                }
        );

        IntStream.range(0, WORKER_COUNT)
                .forEach(i -> {
                    var workerId = i + 1;
                    var future = executor.submit(() -> continuousWorkerLoop(workerId));
                    workerFutures.add(future);
                });

        log.info("Started {} continuous transaction workers", WORKER_COUNT);
    }

    private void continuousWorkerLoop(int workerId) {
        log.info("Worker-{} started continuous processing", workerId);

        while (!isShuttingDown.get() && !Thread.currentThread().isInterrupted()) {
            try {
                var transactions = transactionService.findAllPendingTransactions(BATCH_SIZE);

                if (!transactions.isEmpty()) {
                    log.info("Worker-{}: claimed {} transactions", workerId, transactions.size());

                    processTransactionBatch(workerId, transactions);
                } else {
                    log.debug("Worker-{}: no pending transactions, waiting {}ms", workerId, EMPTY_BATCH_DELAY_MS);
                    Thread.sleep(EMPTY_BATCH_DELAY_MS);
                }

            } catch (InterruptedException e) {
                log.info("Worker-{}: interrupted, stopping", workerId);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Worker-{}: error in worker loop: {}", workerId, e.getMessage(), e);
                try {
                    Thread.sleep(POLLING_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("Worker-{} stopped", workerId);
    }

    private void processTransactionBatch(int workerId, List<Transaction> transactions) {
        var processed = 0;
        var failed = 0;

        for (Transaction transaction : transactions) {
            if (isShuttingDown.get() || Thread.currentThread().isInterrupted()) {
                log.info("Worker-{}: shutdown requested, stopping batch processing", workerId);
                break;
            }

            try {
                processTransactionUseCase.processTransaction(transaction);
                processed++;
                log.debug("Worker-{}: processed transaction {} {}", workerId, transaction.getId(), transaction.getUuid());
            } catch (Exception e) {
                failed++;
                log.error("Worker-{}: failed to process transaction {} {}: {}", workerId, transaction.getId(), transaction.getUuid(), e.getMessage(), e);
            }
        }

        log.info("Worker-{}: batch completed - {} processed, {} failed", workerId, processed, failed);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down transaction workers...");
        isShuttingDown.set(true);

        if (executor != null) {
            workerFutures.forEach(future -> future.cancel(true));

            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Workers didn't shutdown gracefully, forcing shutdown");
                    executor.shutdownNow();

                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.error("Could not terminate workers");
                    }
                }
                log.info("Transaction workers shut down successfully");
            } catch (InterruptedException e) {
                log.warn("Shutdown interrupted, forcing immediate shutdown");
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
