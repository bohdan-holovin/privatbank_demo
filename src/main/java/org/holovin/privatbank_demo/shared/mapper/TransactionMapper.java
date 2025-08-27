package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;

public final class TransactionMapper {

    public static TransactionResponseDto toTransactionDto(Transaction transaction) {
        var dto = new TransactionResponseDto();

        dto.setId(transaction.getId());
        dto.setUuid(transaction.getUuid());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus().name());
        dto.setType(transaction.getType().name());
        dto.setProcessedAt(transaction.getProcessedAt());
        dto.setFromAccountNumber(transaction.getFromAccount() != null ? transaction.getFromAccount().getNumber() : null);
        dto.setToAccountNumber(transaction.getToAccount() != null ? transaction.getToAccount().getNumber() : null);

        return dto;

    }
}
