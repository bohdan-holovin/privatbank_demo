package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.account.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;

public interface TopUpAccountInPort {

    TransactionResponseDto execute(AccountTopUpRequestDto request);
}
