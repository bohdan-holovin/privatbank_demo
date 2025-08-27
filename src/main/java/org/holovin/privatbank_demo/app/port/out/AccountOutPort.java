package org.holovin.privatbank_demo.app.port.out;

import org.holovin.privatbank_demo.domain.model.Account;

import java.util.Optional;

public interface AccountOutPort {

    Optional<Account> findByNumberWithBalance(String number);
}
