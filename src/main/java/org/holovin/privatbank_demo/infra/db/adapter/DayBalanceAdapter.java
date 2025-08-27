package org.holovin.privatbank_demo.infra.db.adapter;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.out.DayBalanceOutPort;
import org.holovin.privatbank_demo.domain.model.DayBalance;
import org.holovin.privatbank_demo.infra.db.repository.DayBalanceRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DayBalanceAdapter implements DayBalanceOutPort {

    private final DayBalanceRepository repository;

    @Override
    public void createDailySnapshotsBulk(LocalDate balanceDate, LocalDateTime createdDate) {
        repository.createDailySnapshotsBulk(balanceDate, createdDate);
    }

    @Override
    public boolean existsByBalanceDate(LocalDate balanceDate) {
        return repository.existsByBalanceDate(balanceDate);
    }

    @Override
    public Optional<DayBalance> findByAccountIdAndBalanceDate(Long accountId, LocalDate balanceDate) {
        return repository.findByAccountIdAndBalanceDate(accountId, balanceDate);
    }
}
