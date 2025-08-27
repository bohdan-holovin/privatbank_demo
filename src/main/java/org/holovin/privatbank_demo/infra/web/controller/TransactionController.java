package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.in.GetTransactionInPort;
import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final GetTransactionInPort getTransactionInPort;

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponseDto> getTransaction(@PathVariable @NotNull @Positive Long id) {
        var accountByDateResponseDto = getTransactionInPort.execute(id);
        return ResponseEntity.ok(accountByDateResponseDto);
    }
}
