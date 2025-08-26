package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.exception.AccountNotFoundException;
import org.holovin.privatbank_demo.app.exception.InactiveAccountException;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TopUpAccountUseCase {

    private final AccountService accountService;

    @Transactional
    public TransactionDto execute(AccountTopUpRequestDto request) {

        var account = accountService.findByNumberWithBalance(request.getAccountNumber());

        try {
            var transaction = account.topUp(request.getAmount(), LocalDateTime.now());
            return TransactionMapper.toTransactionDto(transaction);
        } catch (AccountNotFoundException | InactiveAccountException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing top-up for account: " + e.getMessage(), e);
        }
    }
}