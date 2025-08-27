package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.usecase.UserRegistrationUseCase;
import org.holovin.privatbank_demo.shared.dto.request.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationUseCase userRegistrationService;

    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRegistrationRequestDto request) {
        var userResponseDto = userRegistrationService.execute(request);
        return ResponseEntity.ok(userResponseDto);
    }
}
