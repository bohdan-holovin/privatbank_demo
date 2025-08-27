package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.response.AccountByDateResponseDto;

import java.time.LocalDate;

public interface GetBalanceByDateInPort {

    AccountByDateResponseDto execute(Long accountId, LocalDate date);
}
