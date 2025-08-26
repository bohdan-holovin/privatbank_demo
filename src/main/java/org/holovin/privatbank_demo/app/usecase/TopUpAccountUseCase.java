package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopUpAccountUseCase {

    private final AccountService accountService;

    @Transactional
    public TransactionDto execute(AccountTopUpRequestDto request) {

        var account = accountService.findByNumberWithBalance(request.getAccountNumber());

        try {
            var transaction = account.topUp(request.getAmount());
            return TransactionMapper.toTransactionDto(transaction);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing top-up for account: " + e.getMessage(), e);
        }
    }
}
