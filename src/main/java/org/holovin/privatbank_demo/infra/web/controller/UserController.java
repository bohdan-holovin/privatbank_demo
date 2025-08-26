package org.holovin.privatbank_demo.infra.web.controller;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.usecase.GetAllUsersUseCase;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final GetAllUsersUseCase getAllUsersUseCase;

    @GetMapping("/api/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        var users = getAllUsersUseCase.execute();
        return ResponseEntity.ok(users);
    }
}
