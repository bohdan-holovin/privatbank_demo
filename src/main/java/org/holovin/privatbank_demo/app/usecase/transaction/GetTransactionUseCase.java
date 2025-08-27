package org.holovin.privatbank_demo.app.usecase.transaction;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.in.GetTransactionInPort;
import org.holovin.privatbank_demo.app.service.TransactionService;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTransactionUseCase implements GetTransactionInPort {

    private final TransactionService transactionService;

    @Transactional(readOnly = true)
    public TransactionResponseDto execute(Long transactionId) {
        var transactions = transactionService.findById(transactionId);
        return TransactionMapper.toTransactionDto(transactions);
    }
}
