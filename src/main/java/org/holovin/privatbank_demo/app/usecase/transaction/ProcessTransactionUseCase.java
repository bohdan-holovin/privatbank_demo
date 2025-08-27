package org.holovin.privatbank_demo.app.usecase.transaction;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.TransactionService;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessTransactionUseCase {

    private final TransactionService transactionService;

    @Transactional
    @Retryable(
            retryFor = {CannotAcquireLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000)
    )
    public void execute(Transaction tx) {
        if (tx == null || tx.getId() == null) {
            throw new IllegalArgumentException("transaction must be persisted");
        }
        var transaction = transactionService.findById(tx.getId());
        transaction.process();
    }
}
