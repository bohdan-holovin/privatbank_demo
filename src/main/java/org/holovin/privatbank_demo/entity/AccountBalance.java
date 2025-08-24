package org.holovin.privatbank_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "account_balances",
    uniqueConstraints = @UniqueConstraint(name = "uk_account_date", columnNames = {"account_id", "balance_date"}),
    indexes = {
        @Index(name = "idx_balance_date", columnList = "balance_date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @Column(name = "opening_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "closing_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal closingBalance;

    @Column(name = "total_debits", precision = 15, scale = 2)
    private BigDecimal totalDebits = BigDecimal.ZERO;

    @Column(name = "total_credits", precision = 15, scale = 2)
    private BigDecimal totalCredits = BigDecimal.ZERO;

    @Column(name = "transaction_count")
    private Integer transactionCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
