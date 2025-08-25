package org.holovin.privatbank_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "current_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountCurrentBalance extends AbstractAuditable {

    @Column(name = "available_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "pending_balance", precision = 15, scale = 2)
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "last_transaction_id")
    private Transaction lastTransaction;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
