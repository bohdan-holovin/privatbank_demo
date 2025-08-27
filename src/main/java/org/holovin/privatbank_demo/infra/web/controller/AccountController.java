package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.usecase.GetCurrentBalanceUseCase;
import org.holovin.privatbank_demo.app.usecase.TopUpAccountUseCase;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.AccountResponseDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final GetCurrentBalanceUseCase getCurrentBalanceUseCase;
    private final TopUpAccountUseCase topUpAccountUseCase;

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<AccountResponseDto> getBalance(@PathVariable @Valid @NotNull @Positive Long id) {
        var accountResponseDto = getCurrentBalanceUseCase.execute(id);
        return ResponseEntity.ok(accountResponseDto);
    }

    @PostMapping("/accounts/top-up")
    public ResponseEntity<TransactionResponseDto> topUpAccount(@Valid @RequestBody AccountTopUpRequestDto request) {
        var transactionResponseDto = topUpAccountUseCase.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }
}
