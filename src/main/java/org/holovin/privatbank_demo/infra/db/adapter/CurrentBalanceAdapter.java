package org.holovin.privatbank_demo.infra.db.adapter;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.out.CurrentBalanceOutPort;
import org.holovin.privatbank_demo.domain.model.CurrentBalance;
import org.holovin.privatbank_demo.infra.db.repository.CurrentBalanceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentBalanceAdapter implements CurrentBalanceOutPort {

    private final CurrentBalanceRepository currentBalanceRepository;

    @Override
    public Optional<CurrentBalance> findByAccountId(Long accountId) {
        return currentBalanceRepository.findByAccountId(accountId);
    }
}
