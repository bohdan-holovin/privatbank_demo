package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.AccountCurrentBalance;
import org.holovin.privatbank_demo.shared.dto.response.AccountResponseDto;

public final class AccountMapper {

    public static AccountResponseDto toAccountResponseDto(AccountCurrentBalance accountCurrentBalance) {
        if (accountCurrentBalance == null) {
            return null;
        }
        var account = accountCurrentBalance.getAccount();

        var dto = new AccountResponseDto();
        dto.setId(accountCurrentBalance.getId());
        dto.setAccountNumber(account.getNumber());
        dto.setAvailableBalance(accountCurrentBalance.getAvailableBalance());
        dto.setPendingBalance(accountCurrentBalance.getPendingBalance());
        dto.setStatus(account.getStatus().name());
        dto.setCreatedDate(accountCurrentBalance.getCreatedDate());
        dto.setModifiedDate(accountCurrentBalance.getModifiedDate());

        return dto;
    }
}
