package org.holovin.privatbank_demo.infra.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.in.*;
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

    private final GetCurrentBalanceInPort getCurrentBalanceInPort;
    private final TopUpAccountInPort topUpAccountInPort;
    private final TransferFromInPort transferFromInPort;
    private final WithdrawAccountInPort withdrawAccountInPort;
    private final GetBalanceByDateInPort getBalanceByDateInPort;
    private final GetTransactionsInPort getTransactionsInPort;

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<AccountResponseDto> getCurrentBalance(@PathVariable @NotNull @Positive Long id) {
        var accountResponseDto = getCurrentBalanceInPort.execute(id);
        return ResponseEntity.ok(accountResponseDto);
    }

    @GetMapping("/accounts/{id}/balance/{date}")
    public ResponseEntity<AccountByDateResponseDto> getBalanceByDate(
            @PathVariable @NotNull @Positive Long id,
            @PathVariable @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date
    ) {
        var accountByDateResponseDto = getBalanceByDateInPort.execute(id, date);
        return ResponseEntity.ok(accountByDateResponseDto);
    }

    @PostMapping("/accounts/top-up")
    public ResponseEntity<TransactionResponseDto> topUp(@Valid @RequestBody AccountTopUpRequestDto request) {
        var transactionResponseDto = topUpAccountInPort.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(@Valid @RequestBody AccountTransferRequestDto request) {
        var transactionResponseDto = transferFromInPort.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @PostMapping("/accounts/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(@Valid @RequestBody AccountWithdrawRequestDto request) {
        var transactionResponseDto = withdrawAccountInPort.execute(request);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @PathVariable @NotNull @Positive Long id,
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        var accountByDateResponseDto = getTransactionsInPort.execute(id, limit, offset);
        return ResponseEntity.ok(accountByDateResponseDto);
    }
}
