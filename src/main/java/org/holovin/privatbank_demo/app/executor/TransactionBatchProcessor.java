package org.holovin.privatbank_demo.app.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.usecase.transaction.ProcessTransactionUseCase;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionBatchProcessor {

    private final ProcessTransactionUseCase processTransactionUseCase;
    private final WorkerStateManager stateManager;

    public void processBatch(int workerId, List<Transaction> transactions) {
        var result = new BatchProcessingResult();

        for (Transaction transaction : transactions) {
            if (!stateManager.shouldContinueProcessing()) {
                log.info("Worker-{}: shutdown requested, stopping batch processing", workerId);
                break;
            }

            try {
                processTransactionUseCase.processTransaction(transaction);
                result.incrementProcessed();
                log.debug("Worker-{}: processed transaction {} {}", workerId, transaction.getId(), transaction.getUuid());
            } catch (Exception e) {
                result.incrementFailed();
                log.error("Worker-{}: failed to process transaction {} {}: {}", workerId, transaction.getId(), transaction.getUuid(), e.getMessage(), e);
            }
        }

        log.info("Worker-{}: batch completed - {} processed, {} failed", workerId, result.getProcessed(), result.getFailed());
    }

    @lombok.Data
    public static class BatchProcessingResult {
        private int processed = 0;
        private int failed = 0;

        public void incrementProcessed() {
            processed++;
        }

        public void incrementFailed() {
            failed++;
        }
    }
}
