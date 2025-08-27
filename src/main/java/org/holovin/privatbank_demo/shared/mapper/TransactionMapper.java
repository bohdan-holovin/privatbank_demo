package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;

public class TransactionMapper {

    public static TransactionResponseDto toTransactionDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .uuid(transaction.getUuid())
                .amount(transaction.getAmount())
                .status(transaction.getStatus().name())
                .processedAt(transaction.getProcessedAt())
                .fromAccountNumber(transaction.getFromAccount() != null ? transaction.getFromAccount().getNumber() : null)
                .toAccountNumber(transaction.getToAccount() != null ? transaction.getToAccount().getNumber() : null)
                .build();
    }
}
