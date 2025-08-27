package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.domain.repository.TransactionRepository;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessTransactionUseCase {

    private final TransactionRepository transactionRepository;

    @Retryable(
            retryFor = {CannotAcquireLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000)
    )
    @Transactional
    public void processTransaction(Transaction tx) {
        if (tx == null || tx.getId() == null) {
            throw new IllegalArgumentException("transaction must be persisted");
        }
        var transaction = transactionRepository.findById(tx.getId()).orElseThrow();
        transaction.process();
        transactionRepository.save(transaction);
    }
}
