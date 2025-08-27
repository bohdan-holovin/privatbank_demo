package org.holovin.privatbank_demo.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.domain.model.DayBalance;
import org.holovin.privatbank_demo.domain.repository.DayBalanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DayBalanceService {

    private final DayBalanceRepository dayBalanceRepository;

    public void createDailySnapshots(LocalDate balanceDate) {
        dayBalanceRepository.createDailySnapshotsBulk(balanceDate, LocalDateTime.now());
    }

    public boolean existsByBalanceDate(LocalDate balanceDate) {
        return dayBalanceRepository.existsByBalanceDate(balanceDate);
    }

    public DayBalance findByAccountIdAndBalanceDate(Long accountId, LocalDate balanceDate) {
        return dayBalanceRepository.findByAccountIdAndBalanceDate(accountId, balanceDate)
                .orElseThrow(() -> new EntityNotFoundException("DayBalance not found for accountId=" + accountId + ", date=" + balanceDate));
    }
}
