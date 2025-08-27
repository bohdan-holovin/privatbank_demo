package org.holovin.privatbank_demo.app.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.exception.DuplicateTransactionException;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final TransactionService transactionService;

    public Transaction checkOrGetExisting(String uuid, BigDecimal amount, Transaction.Type type) {
        var optionalTransaction = transactionService.findByUuidOptional(uuid);

        if (optionalTransaction.isEmpty()) {
            return null;
        }

        var existingTransaction = optionalTransaction.get();
        if (existingTransaction.isSameOperation(amount, type)) {
            return existingTransaction;
        } else {
            throw new DuplicateTransactionException("Idempotency key already used for another operation");
        }
    }
}
