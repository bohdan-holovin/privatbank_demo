package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.UserService;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;
import org.holovin.privatbank_demo.shared.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<UserResponseDto> execute() {
        return userService.findAll().stream()
                .map(UserMapper::toUserResponseDto)
                .toList();
    }
}
