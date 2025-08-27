package org.holovin.privatbank_demo.shared.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.holovin.privatbank_demo.shared.dto.response.account.AccountResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private List<AccountResponseDto> accounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
