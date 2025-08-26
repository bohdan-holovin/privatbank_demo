package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findByNumberWithBalance(String number) {
        return accountRepository.findByNumberWithBalance(number)
                .orElseThrow(() -> new EntityNotFoundException("Account with  number " + number));
    }
}
