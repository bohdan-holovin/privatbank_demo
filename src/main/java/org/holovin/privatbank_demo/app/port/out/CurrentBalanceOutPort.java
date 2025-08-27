package org.holovin.privatbank_demo.app.port.out;

import org.holovin.privatbank_demo.domain.model.CurrentBalance;

import java.util.Optional;

public interface CurrentBalanceOutPort {

    Optional<CurrentBalance> findByAccountId(Long accountId);
}
