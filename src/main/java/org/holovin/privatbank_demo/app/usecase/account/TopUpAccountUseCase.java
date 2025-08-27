package org.holovin.privatbank_demo.app.usecase.account;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.in.TopUpAccountInPort;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.app.service.IdempotencyService;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopUpAccountUseCase implements TopUpAccountInPort {

    private final IdempotencyService idempotencyService;
    private final AccountService accountService;

    @Transactional
    @Retryable(retryFor = {CannotAcquireLockException.class}, maxAttempts = 2, backoff = @Backoff(delay = 100))
    public TransactionResponseDto execute(AccountTopUpRequestDto request) {

        var uuid = request.getUuid();
        var amount = request.getAmount();

        var existingTransaction = idempotencyService.checkOrGetExisting(uuid, amount, Transaction.Type.TOP_UP);
        if (existingTransaction != null) return TransactionMapper.toTransactionDto(existingTransaction);

        var account = accountService.findByNumberWithBalance(request.getAccountNumber());

        try {
            var transaction = account.topUp(amount, uuid);
            return TransactionMapper.toTransactionDto(transaction);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing top-up for account: " + e.getMessage(), e);
        }
    }
}
