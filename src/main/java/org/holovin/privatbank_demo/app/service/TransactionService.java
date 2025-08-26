package org.holovin.privatbank_demo.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> findPendingTransactions(int limit) {
        return transactionRepository.findPendingTransactions(limit);
    }
}
