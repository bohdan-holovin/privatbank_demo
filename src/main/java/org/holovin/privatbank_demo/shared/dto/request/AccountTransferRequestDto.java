package org.holovin.privatbank_demo.shared.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferRequestDto {

    @UUID
    private String uuid;

    @NotBlank
    private String fromAccountNumber;

    @NotBlank
    private String toAccountNumber;

    @NotNull
    @Positive
    private BigDecimal amount;
}
