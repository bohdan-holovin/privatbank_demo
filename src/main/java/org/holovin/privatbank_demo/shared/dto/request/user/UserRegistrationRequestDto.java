package org.holovin.privatbank_demo.shared.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.holovin.privatbank_demo.infra.web.controller.annotation.UniqueUsername;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {

    @NotBlank
    @UniqueUsername
    private String username;

    @NotNull
    @Positive
    private Integer accountCount;
}
