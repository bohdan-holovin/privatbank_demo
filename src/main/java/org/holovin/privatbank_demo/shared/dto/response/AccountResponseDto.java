package org.holovin.privatbank_demo.shared.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private BigDecimal availableBalance;
    private BigDecimal pendingBalance;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
