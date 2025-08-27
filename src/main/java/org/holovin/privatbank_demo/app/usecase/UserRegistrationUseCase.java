package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.UserService;
import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.shared.dto.request.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;
import org.holovin.privatbank_demo.shared.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistrationUseCase {

    private final UserService userService;

    @Transactional
    public UserResponseDto execute(UserRegistrationRequestDto request) {
        var user = User.create(request.getUsername(), request.getAccountCount());
        userService.save(user);

        return UserMapper.toUserResponseDto(user);
    }
}
