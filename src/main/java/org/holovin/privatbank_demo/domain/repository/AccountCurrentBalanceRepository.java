package org.holovin.privatbank_demo.domain.repository;

import org.holovin.privatbank_demo.domain.model.AccountCurrentBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountCurrentBalanceRepository extends JpaRepository<AccountCurrentBalance, Long> {

    @Query("""
                SELECT cb
                FROM AccountCurrentBalance cb
                JOIN FETCH cb.account a
                WHERE a.id = :id
            """)
    Optional<AccountCurrentBalance> findByAccountId(Long id);
}
