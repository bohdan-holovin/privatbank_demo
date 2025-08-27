package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> findAllPendingTransactions(int limit) {
        return transactionRepository.findAllPendingTransactionsWithLock(limit);
    }

    public List<Transaction> findAllByAccountIdWithLimitAndOffset(Long accountId, int limit, int offset) {
        return transactionRepository.findAllByAccountIdWithLimitAndOffset(accountId, limit, offset);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with" + id + "not fount"));
    }

    public Optional<Transaction> findByUuidOptional(String uuid) {
        return transactionRepository.findByUuid(uuid);
    }
}
