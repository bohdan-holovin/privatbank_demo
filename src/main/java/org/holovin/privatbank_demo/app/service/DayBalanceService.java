package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.port.out.DayBalanceOutPort;
import org.holovin.privatbank_demo.domain.model.DayBalance;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DayBalanceService {

    private final DayBalanceOutPort dayBalanceOutPort;

    public void createDailySnapshots(LocalDate balanceDate) {
        dayBalanceOutPort.createDailySnapshotsBulk(balanceDate, LocalDateTime.now());
    }

    public boolean existsByBalanceDate(LocalDate balanceDate) {
        return dayBalanceOutPort.existsByBalanceDate(balanceDate);
    }

    public DayBalance findByAccountIdAndBalanceDate(Long accountId, LocalDate balanceDate) {
        return dayBalanceOutPort.findByAccountIdAndBalanceDate(accountId, balanceDate)
                .orElseThrow(() -> new EntityNotFoundException("DayBalance not found for accountId=" + accountId + ", date=" + balanceDate));
    }
}
