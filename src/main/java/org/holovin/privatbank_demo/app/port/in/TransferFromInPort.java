package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.TransactionResponseDto;

public interface TransferFromInPort {

    TransactionResponseDto execute(AccountTransferRequestDto request);
}
