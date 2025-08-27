package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.app.service.IdempotencyService;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.request.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.holovin.privatbank_demo.shared.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferUseCase {

    private final IdempotencyService idempotencyService;
    private final AccountService accountService;

    @Transactional
    public TransactionResponseDto execute(AccountTransferRequestDto request) {

        var uuid = request.getUuid();
        var amount = request.getAmount();

        var existingTransaction = idempotencyService.checkOrGetExisting(uuid, amount, Transaction.Type.TRANSFER);
        if (existingTransaction != null) return TransactionMapper.toTransactionDto(existingTransaction);

        var fromAccount = accountService.findByNumberWithBalance(request.getFromAccountNumber());
        var toAccount = accountService.findByNumberWithBalance(request.getToAccountNumber());

        try {
            var transaction = fromAccount.transferTo(toAccount, amount, uuid);
            return TransactionMapper.toTransactionDto(transaction);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while processing transfer: " + e.getMessage(), e);
        }
    }
}
