package org.holovin.privatbank_demo.app.usecase.account;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.in.WithdrawAccountInPort;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.app.service.IdempotencyService;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.request.account.AccountWithdrawRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawAccountUseCase implements WithdrawAccountInPort {

    private final IdempotencyService idempotencyService;
    private final AccountService accountService;

    @Transactional
    @Retryable(
            retryFor = {CannotAcquireLockException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 100)
    )
    public TransactionResponseDto execute(AccountWithdrawRequestDto request) {

        var uuid = request.getUuid();
        var amount = request.getAmount();

        var existingTransaction = idempotencyService.checkOrGetExisting(uuid, amount, Transaction.Type.WITHDRAW);
        if (existingTransaction != null) return TransactionMapper.toTransactionDto(existingTransaction);

        var account = accountService.findByNumberWithBalance(request.getAccountNumber());

        try {
            var transaction = account.withdraw(amount, uuid);
            return TransactionMapper.toTransactionDto(transaction);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing withdraw for account: " + e.getMessage(), e);
        }
    }
}
