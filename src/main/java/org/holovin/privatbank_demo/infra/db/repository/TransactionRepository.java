package org.holovin.privatbank_demo.infra.db.repository;

import org.holovin.privatbank_demo.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            SELECT *
            FROM transactions
            WHERE status = 'PENDING'
            ORDER BY created_date
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Transaction> findAllPendingTransactionsWithLock(@Param("limit") int limit);

    Optional<Transaction> findByUuid(String uuid);

    @Query(value = """
            SELECT *
            FROM transactions t
            WHERE t.from_account_id = :accountId OR t.to_account_id = :accountId
            ORDER BY t.created_date DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Transaction> findAllByAccountIdWithLimitAndOffset(@Param("accountId") Long accountId, @Param("limit") int limit, @Param("offset") int offset);
}

