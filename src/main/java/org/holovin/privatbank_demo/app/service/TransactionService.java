package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.port.out.TransactionOutPort;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionOutPort transactionOutPort;

    public void save(Transaction transaction) {
        transactionOutPort.save(transaction);
    }

    public List<Transaction> findAllPendingTransactions(int limit) {
        return transactionOutPort.findAllPendingTransactionsWithLock(limit);
    }

    public List<Transaction> findAllByAccountIdWithLimitAndOffset(Long accountId, int limit, int offset) {
        return transactionOutPort.findAllByAccountIdWithLimitAndOffset(accountId, limit, offset);
    }

    public Transaction findById(Long id) {
        return transactionOutPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with" + id + "not fount"));
    }

    public Optional<Transaction> findByUuidOptional(String uuid) {
        return transactionOutPort.findByUuid(uuid);
    }
}
