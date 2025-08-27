package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.DayBalanceService;
import org.holovin.privatbank_demo.shared.dto.response.AccountByDateResponseDto;
import org.holovin.privatbank_demo.shared.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetBalanceByDateUseCase {

    private final DayBalanceService dayBalanceService;

    @Transactional(readOnly = true)
    public AccountByDateResponseDto execute(Long accountId, LocalDate date) {
        var dayBalance = dayBalanceService.findByAccountIdAndBalanceDate(accountId, date);
        return AccountMapper.toAccountByDateResponseDto(dayBalance);
    }
}
