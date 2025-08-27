package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.domain.model.CurrentBalance;
import org.holovin.privatbank_demo.domain.repository.CurrentBalanceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentBalanceService {

    private final CurrentBalanceRepository currentBalanceRepository;

    public CurrentBalance findByAccountId(Long accountId) {
        return currentBalanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("CurrentBalance not found: " + accountId));
    }
}
