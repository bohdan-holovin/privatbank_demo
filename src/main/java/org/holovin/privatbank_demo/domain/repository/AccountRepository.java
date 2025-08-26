package org.holovin.privatbank_demo.domain.repository;

import org.holovin.privatbank_demo.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.currentBalance WHERE a.number = :number")
    Optional<Account> findByNumberWithBalance(@Param("number") String number);
}
