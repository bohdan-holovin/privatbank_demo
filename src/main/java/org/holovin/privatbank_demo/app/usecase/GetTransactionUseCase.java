package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.TransactionService;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTransactionUseCase {

    private final TransactionService transactionService;

    @Transactional
    public TransactionResponseDto execute(Long transactionId) {
        var transactions = transactionService.findById(transactionId);
        return TransactionMapper.toTransactionDto(transactions);
    }
}
