package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.shared.dto.request.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferUseCase {

    private final AccountService accountService;

    @Transactional
    public TransactionResponseDto execute(AccountTransferRequestDto request) {

        var fromAccount = accountService.findByNumberWithBalance(request.getFromAccountNumber());
        var toAccount = accountService.findByNumberWithBalance(request.getToAccountNumber());

        try {
            var transaction = fromAccount.transferTo(toAccount, request.getAmount());
            return TransactionMapper.toTransactionDto(transaction);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing top-up for account: " + e.getMessage(), e);
        }
    }
}
