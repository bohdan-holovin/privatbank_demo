package org.holovin.privatbank_demo.app.usecase.user;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.in.UserRegistrationInPort;
import org.holovin.privatbank_demo.app.service.UserService;
import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.shared.dto.request.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;
import org.holovin.privatbank_demo.shared.mapper.UserMapper;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistrationUseCase implements UserRegistrationInPort {

    private final UserService userService;

    @Transactional
    @Retryable(
            retryFor = {CannotAcquireLockException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 100)
    )
    public UserResponseDto execute(UserRegistrationRequestDto request) {
        var user = User.create(request.getUsername(), request.getAccountCount());
        userService.save(user);

        return UserMapper.toUserResponseDto(user);
    }
}
