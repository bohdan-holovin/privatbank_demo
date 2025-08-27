package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.shared.dto.request.AccountWithdrawRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawAccountUseCase {

    private final AccountService accountService;

    @Transactional
    public TransactionResponseDto execute(AccountWithdrawRequestDto request) {

        var account = accountService.findByNumberWithBalance(request.getAccountNumber());

        try {
            var transaction = account.withdraw(request.getAmount());
            return TransactionMapper.toTransactionDto(transaction);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing withdraw for account: " + e.getMessage(), e);
        }
    }
}
