package org.holovin.privatbank_demo.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}