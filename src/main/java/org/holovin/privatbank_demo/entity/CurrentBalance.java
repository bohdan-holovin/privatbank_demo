package org.holovin.privatbank_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentBalance {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "available_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "pending_balance", precision = 15, scale = 2)
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "last_transaction_id")
    private Transaction lastTransaction;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
