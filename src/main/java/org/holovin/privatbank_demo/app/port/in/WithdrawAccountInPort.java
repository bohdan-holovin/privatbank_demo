package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.AccountWithdrawRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;

public interface WithdrawAccountInPort {

    TransactionResponseDto execute(AccountWithdrawRequestDto request);
}
