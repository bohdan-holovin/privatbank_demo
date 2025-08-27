package org.holovin.privatbank_demo.infra.db.adapter;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.out.AccountOutPort;
import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.infra.db.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountAdapter implements AccountOutPort {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> findByNumberWithBalance(String number) {
        return accountRepository.findByNumberWithBalance(number);
    }
}
