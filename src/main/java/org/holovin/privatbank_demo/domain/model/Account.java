package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.exception.InactiveAccountException;
import org.holovin.privatbank_demo.domain.model.base.AbstractAuditable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractAuditable {

    private String number;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "fromAccount",
            fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Transaction> outgoingTransactions;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "toAccount",
            fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Transaction> incomingTransactions;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "account",
            fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<DayBalance> balances;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private CurrentBalance currentBalance;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    public static Account create() {
        var account = new Account();
        account.setNumber(generateNumber());
        account.setStatus(Account.Status.ACTIVE);

        var currentBalance = CurrentBalance.create();
        account.addCurrentBalance(currentBalance);
        return account;
    }

    private static String generateNumber() {
        var uuid = UUID.randomUUID();
        long hash = Math.abs((long) uuid.hashCode());
        return String.format("%016d", hash);
    }

    private void addCurrentBalance(CurrentBalance currentBalance) {
        currentBalance.setAccount(this);
        this.currentBalance = currentBalance;
    }

    public Transaction topUp(BigDecimal amount) {
        if (this.status != Status.ACTIVE) {
            throw new InactiveAccountException(number);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        var transaction = Transaction.createTopUp(UUID.randomUUID().toString(), amount, this);
        addIncomingTransaction(transaction);
        return transaction;
    }

    private void addIncomingTransaction(Transaction transaction) {
        transaction.setToAccount(this);
        this.incomingTransactions.add(transaction);
    }

    public enum Status {
        ACTIVE,
        CLOSED,
    }
}
