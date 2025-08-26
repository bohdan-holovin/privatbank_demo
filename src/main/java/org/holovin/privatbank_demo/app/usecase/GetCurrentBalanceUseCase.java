package org.holovin.privatbank_demo.app.usecase;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.AccountCurrentBalanceService;
import org.holovin.privatbank_demo.shared.dto.response.AccountResponseDto;
import org.holovin.privatbank_demo.shared.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCurrentBalanceUseCase {

    private final AccountCurrentBalanceService accountCurrentBalanceService;

    @Transactional(readOnly = true)
    public AccountResponseDto execute(Long accountId) {
        var currentBalance = accountCurrentBalanceService.findByAccountId(accountId);
        return AccountMapper.toAccountResponseDto(currentBalance);
    }
}
