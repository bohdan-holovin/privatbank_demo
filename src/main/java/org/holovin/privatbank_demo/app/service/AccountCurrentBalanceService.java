package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.domain.model.AccountCurrentBalance;
import org.holovin.privatbank_demo.domain.repository.AccountCurrentBalanceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCurrentBalanceService {

    private final AccountCurrentBalanceRepository accountCurrentBalanceRepository;

    public AccountCurrentBalance findByAccountId(Long accountId) {
        return accountCurrentBalanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + accountId));
    }
}
