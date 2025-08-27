package org.holovin.privatbank_demo.shared.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountByDateResponseDto {

    private Long id;
    private Long accountId;
    private BigDecimal availableBalance;
    private BigDecimal pendingBalance;
    private LocalDate date;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
