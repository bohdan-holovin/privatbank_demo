package org.holovin.privatbank_demo.shared.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTopUpRequestDto {

    @NotBlank(message = "Account number cannot be empty")
    private String accountNumber;

    @NotNull(message = "The top-up amount cannot be empty")
    @Positive(message = "The top-up amount must be positive")
    private BigDecimal amount;
}
