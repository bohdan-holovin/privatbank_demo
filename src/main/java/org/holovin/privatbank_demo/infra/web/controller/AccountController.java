package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.usecase.*;
import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.request.AccountWithdrawRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.AccountByDateResponseDto;
import org.holovin.privatbank_demo.shared.dto.response.AccountResponseDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final GetCurrentBalanceUseCase getCurrentBalanceUseCase;
    private final TopUpAccountUseCase topUpAccountUseCase;
    private final TransferUseCase transferUseCase;
    private final WithdrawAccountUseCase withdrawAccountUseCase;
    private final GetBalanceByDateUseCase getBalanceByDateUseCase;
    private final GetTransactionsUseCase getTransactionsUseCase;

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<AccountResponseDto> getCurrentBalance(@PathVariable @NotNull @Positive Long id) {
        var accountResponseDto = getCurrentBalanceUseCase.execute(id);
        return ResponseEntity.ok(accountResponseDto);
    }

    @GetMapping("/accounts/{id}/balance/{date}")
    public ResponseEntity<AccountByDateResponseDto> getBalanceByDate(
            @PathVariable @NotNull @Positive Long id,
            @PathVariable @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date
    ) {
        var accountByDateResponseDto = getBalanceByDateUseCase.execute(id, date);
        return ResponseEntity.ok(accountByDateResponseDto);
    }

    @PostMapping("/accounts/top-up")
    public ResponseEntity<TransactionResponseDto> topUp(@Valid @RequestBody AccountTopUpRequestDto request) {
        var transactionResponseDto = topUpAccountUseCase.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(@Valid @RequestBody AccountTransferRequestDto request) {
        var transactionResponseDto = transferUseCase.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @PostMapping("/accounts/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(@Valid @RequestBody AccountWithdrawRequestDto request) {
        var transactionResponseDto = withdrawAccountUseCase.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @PathVariable @NotNull @Positive Long id,
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        var accountByDateResponseDto = getTransactionsUseCase.execute(id, limit, offset);
        return ResponseEntity.ok(accountByDateResponseDto);
    }
}
