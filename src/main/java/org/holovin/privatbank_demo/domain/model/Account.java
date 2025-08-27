package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.exception.InactiveAccountException;
import org.holovin.privatbank_demo.domain.exception.InsufficientFundsException;
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

    public Transaction topUp(BigDecimal amount, String uuid) {
        if (this.status != Status.ACTIVE) {
            throw new InactiveAccountException(number);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        var transaction = Transaction.createTopUp(uuid, amount, this);
        addIncomingTransaction(transaction);
        return transaction;
    }

    public Transaction transferTo(Account targetAccount, BigDecimal amount, String uuid) {
        if (this.status != Status.ACTIVE) {
            throw new InactiveAccountException(this.number);
        }
        if (targetAccount.status != Status.ACTIVE) {
            throw new InactiveAccountException(targetAccount.number);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.currentBalance.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds to reserve");
        }

        var transaction = Transaction.createTransfer(uuid, amount, this, targetAccount);

        addOutgoingTransaction(transaction);
        targetAccount.addIncomingTransaction(transaction);

        return transaction;
    }

    public Transaction withdraw(BigDecimal amount, String uuid) {
        if (this.status != Status.ACTIVE) {
            throw new InactiveAccountException(number);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (this.currentBalance.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds to reserve");
        }

        var transaction = Transaction.createWithdraw(uuid, amount, this);
        addOutgoingTransaction(transaction);
        return transaction;
    }

    private void addOutgoingTransaction(Transaction transaction) {
        transaction.setFromAccount(this);
        this.outgoingTransactions.add(transaction);
    }

    private void addIncomingTransaction(Transaction transaction) {
        transaction.setToAccount(this);
        this.incomingTransactions.add(transaction);
    }

    private void addCurrentBalance(CurrentBalance currentBalance) {
        currentBalance.setAccount(this);
        this.currentBalance = currentBalance;
    }

    public enum Status {
        ACTIVE,
        CLOSED,
    }
}
