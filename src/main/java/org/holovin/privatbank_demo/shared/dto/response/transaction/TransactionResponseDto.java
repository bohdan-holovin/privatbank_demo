package org.holovin.privatbank_demo.shared.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {

    private Long id;
    private String uuid;
    private BigDecimal amount;
    private String status;
    private LocalDateTime processedAt;
    private String description;
    private String fromAccountNumber;
    private String toAccountNumber;
}
