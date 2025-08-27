package org.holovin.privatbank_demo.infra.db.repository;

import org.holovin.privatbank_demo.domain.model.CurrentBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CurrentBalanceRepository extends JpaRepository<CurrentBalance, Long> {

    @Query("""
                SELECT cb
                FROM CurrentBalance cb
                JOIN FETCH cb.account a
                WHERE a.id = :id
            """)
    Optional<CurrentBalance> findByAccountId(@Param("id") Long id);
}
