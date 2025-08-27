package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.exception.InsufficientFundsException;
import org.holovin.privatbank_demo.domain.model.base.AbstractAuditable;

import java.math.BigDecimal;

@Entity
@Table(name = "current_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CurrentBalance extends AbstractAuditable {

    private BigDecimal availableBalance;

    private BigDecimal pendingBalance;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "last_transaction_id")
    private Transaction lastTransaction;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "account_id")
    private Account account;

    public static CurrentBalance create() {
        var currentBalance = new CurrentBalance();
        currentBalance.setAvailableBalance(BigDecimal.ZERO);
        currentBalance.setPendingBalance(BigDecimal.ZERO);
        return currentBalance;
    }

    public void reserveFunds(BigDecimal amount) {
        if (availableBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds to reserve");
        }
        availableBalance = availableBalance.subtract(amount);
        pendingBalance = pendingBalance.add(amount);
    }

    public void commitCredit(BigDecimal amount, Transaction transaction) {
        pendingBalance = pendingBalance.subtract(amount).max(BigDecimal.ZERO);
        availableBalance = availableBalance.add(amount);
        lastTransaction = transaction;
    }

    public void commitDebit(BigDecimal amount, Transaction transaction) {
        pendingBalance = pendingBalance.subtract(amount);
        lastTransaction = transaction;
    }
}
