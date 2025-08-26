package org.holovin.privatbank_demo.app.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionProcessor {
    private final TransactionRepository transactionRepository;

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
