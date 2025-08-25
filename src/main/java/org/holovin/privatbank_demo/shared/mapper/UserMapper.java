package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.shared.dto.UserDto;

import java.util.stream.Collectors;

public final class UserMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        var dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setModifiedDate(user.getModifiedDate());

        if (user.getAccounts() != null) {
            dto.setAccounts(user.getAccounts().stream()
                    .map(AccountMapper::toAccountDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
