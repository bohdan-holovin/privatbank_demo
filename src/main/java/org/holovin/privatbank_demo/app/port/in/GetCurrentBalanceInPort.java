package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.response.AccountResponseDto;

public interface GetCurrentBalanceInPort {

    AccountResponseDto execute(Long accountId);
}
