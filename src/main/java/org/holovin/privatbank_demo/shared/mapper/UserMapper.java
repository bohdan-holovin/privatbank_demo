package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;

import java.util.stream.Collectors;

public final class UserMapper {

    public static UserResponseDto toUserResponseDto(User user) {
        var dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setModifiedDate(user.getModifiedDate());

        if (user.getAccounts() != null) {
            dto.setAccounts(user.getAccounts().stream()
                    .map(AccountMapper::toAccountResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
