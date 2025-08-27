package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;

public interface GetTransactionInPort {

    TransactionResponseDto execute(Long transactionId);
}
