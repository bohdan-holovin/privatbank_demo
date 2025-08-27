package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.account.AccountTransferRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;

public interface TransferFromInPort {

    TransactionResponseDto execute(AccountTransferRequestDto request);
}
