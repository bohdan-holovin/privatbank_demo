package org.holovin.privatbank_demo.app.executor;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.service.TransactionService;
import org.holovin.privatbank_demo.config.properties.WorkerProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class TransactionWorkerTask {

    private final TransactionService transactionService;
    private final TransactionBatchProcessor batchProcessor;
    private final WorkerStateManager stateManager;
    private final WorkerProperties config;

    @Setter
    private int workerId;

    @Async("transactionWorkerExecutor")
    public void startProcessing() {
        Thread.currentThread().setName("TransactionWorker-" + workerId);
        log.info("Worker-{} started continuous processing", workerId);

        try {
            while (stateManager.shouldContinueProcessing()) {
                processWorkCycle();
            }
        } catch (InterruptedException e) {
            log.info("Worker-{}: interrupted, stopping", workerId);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Worker-{}: unexpected error in worker loop: {}", workerId, e.getMessage(), e);
        } finally {
            log.info("Worker-{} stopped", workerId);
        }
    }

    private void processWorkCycle() throws InterruptedException {
        try {
            var transactions = transactionService.findAllPendingTransactions(config.getBatchSize());

            if (!transactions.isEmpty()) {
                log.info("Worker-{}: claimed {} transactions", workerId, transactions.size());
                batchProcessor.processBatch(workerId, transactions);
            } else {
                log.debug("Worker-{}: no pending transactions, waiting {}ms", workerId, config.getEmptyBatchDelayMs());
                Thread.sleep(config.getEmptyBatchDelayMs());
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Worker-{}: error in work cycle: {}", workerId, e.getMessage(), e);
            Thread.sleep(config.getPollingDelayMs());
        }
    }
}
