package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.shared.dto.response.AccountResponseDto;

public final class AccountMapper {

    public static AccountResponseDto toAccountResponseDto(Account account) {
        if (account == null) {
            return null;
        }

        var dto = new AccountResponseDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getNumber());
        dto.setBalance(account.getCurrentBalance().getAvailableBalance());
        dto.setCreatedDate(account.getCreatedDate());
        dto.setModifiedDate(account.getModifiedDate());

        return dto;
    }
}
