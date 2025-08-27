package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.AccountTopUpRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;

public interface TopUpAccountInPort {

    TransactionResponseDto execute(AccountTopUpRequestDto request);
}
