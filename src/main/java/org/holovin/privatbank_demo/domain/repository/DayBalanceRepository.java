package org.holovin.privatbank_demo.domain.repository;

import org.holovin.privatbank_demo.domain.model.DayBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DayBalanceRepository extends JpaRepository<DayBalance, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO day_balances (
                account_id,
                balance_date,
                available_balance,
                pending_balance,
                created_date,
                modified_date,
                created_by_id,
                modified_by_id
            )
            SELECT
                a.id,
                :balanceDate,
                cb.available_balance,
                cb.pending_balance,
                :createdAt,
                :createdAt,
                NULL,
                NULL
            FROM accounts a
            INNER JOIN current_balances cb ON a.id = cb.account_id
            WHERE a.status = 'ACTIVE'
            """, nativeQuery = true)
    int createDailySnapshotsBulk(@Param("balanceDate") LocalDate balanceDate, @Param("createdAt") LocalDateTime createdAt);

    @Query("SELECT COUNT(db) > 0 FROM DayBalance db WHERE db.balanceDate = :balanceDate")
    boolean existsByBalanceDate(@Param("balanceDate") LocalDate balanceDate);

    Optional<DayBalance> findByAccountIdAndBalanceDate(Long accountId, LocalDate balanceDate);
}
