package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.domain.model.CurrentBalance;
import org.holovin.privatbank_demo.domain.model.DayBalance;
import org.holovin.privatbank_demo.shared.dto.response.account.AccountByDateResponseDto;
import org.holovin.privatbank_demo.shared.dto.response.account.AccountResponseDto;

public final class AccountMapper {

    public static AccountResponseDto toAccountResponseDto(CurrentBalance currentBalance) {
        var account = currentBalance.getAccount();

        var dto = new AccountResponseDto();
        dto.setId(currentBalance.getId());
        dto.setAccountNumber(account.getNumber());
        dto.setAvailableBalance(currentBalance.getAvailableBalance());
        dto.setPendingBalance(currentBalance.getPendingBalance());
        dto.setStatus(account.getStatus().name());
        dto.setCreatedDate(currentBalance.getCreatedDate());
        dto.setModifiedDate(currentBalance.getModifiedDate());

        return dto;
    }

    public static AccountResponseDto toAccountResponseDto(Account account) {
        var dto = new AccountResponseDto();

        dto.setId(account.getId());
        dto.setAccountNumber(account.getNumber());
        dto.setAvailableBalance(account.getCurrentBalance().getAvailableBalance());
        dto.setPendingBalance(account.getCurrentBalance().getPendingBalance());
        dto.setStatus(account.getStatus().name());
        dto.setCreatedDate(account.getCreatedDate());
        dto.setModifiedDate(account.getModifiedDate());

        return dto;
    }

    public static AccountByDateResponseDto toAccountByDateResponseDto(DayBalance dayBalance) {
        var dto = new AccountByDateResponseDto();

        dto.setId(dayBalance.getId());
        dto.setAccountId(dayBalance.getAccount().getId());
        dto.setAvailableBalance(dayBalance.getAvailableBalance());
        dto.setPendingBalance(dayBalance.getPendingBalance());
        dto.setDate(dayBalance.getBalanceDate());
        dto.setCreatedDate(dayBalance.getCreatedDate());
        dto.setModifiedDate(dayBalance.getModifiedDate());

        return dto;
    }
}
