package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.response.transaction.TransactionResponseDto;

import java.util.List;

public interface GetTransactionsInPort {

    List<TransactionResponseDto> execute(Long accountId, Integer limit, Integer offset);
}
