package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.usecase.TopUpAccountUseCase;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final TopUpAccountUseCase topUpAccountUseCase;

    @PostMapping("/api/top-up")
    public ResponseEntity<TransactionDto> topUpAccount(@Valid @RequestBody AccountTopUpRequestDto request) {

        var transaction = topUpAccountUseCase.execute(request);

        return ResponseEntity.ok(transaction);
    }
}