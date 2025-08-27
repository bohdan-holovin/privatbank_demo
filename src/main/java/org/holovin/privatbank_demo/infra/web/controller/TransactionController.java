package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.usecase.GetTransactionUseCase;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final GetTransactionUseCase getTransactionUseCase;

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponseDto> getTransaction(@PathVariable @NotNull @Positive Long id) {
        var accountByDateResponseDto = getTransactionUseCase.execute(id);
        return ResponseEntity.ok(accountByDateResponseDto);
    }
}
