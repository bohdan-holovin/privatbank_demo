package org.holovin.privatbank_demo.shared.mapper;

import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.shared.dto.AccountDto;

public final class AccountMapper {

    public static AccountDto toAccountDto(Account account) {
        if (account == null) {
            return null;
        }

        var dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getNumber());
        dto.setBalance(account.getAccountCurrentBalance().getAvailableBalance());
        dto.setCreatedDate(account.getCreatedDate());
        dto.setModifiedDate(account.getModifiedDate());

        return dto;
    }
}
