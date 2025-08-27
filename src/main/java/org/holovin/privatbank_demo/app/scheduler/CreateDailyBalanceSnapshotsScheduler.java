package org.holovin.privatbank_demo.app.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.service.DayBalanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateDailyBalanceSnapshotsScheduler {

    private final DayBalanceService dayBalanceService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createDailyBalanceSnapshots() {
        log.info("Creating daily balance snapshots");
        var today = LocalDate.now();

        if (dayBalanceService.existsByBalanceDate(today)) {
            return;
        }

        try {
            dayBalanceService.createDailySnapshots(today);
        } catch (Exception e) {
            log.warn("An unexpected error occurred while creating daily balance snapshots: {}", e.getMessage());
        }

        log.info("Daily balance snapshots created");
    }
}
