package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.app.exception.InactiveAccountException;
import org.holovin.privatbank_demo.domain.model.base.AbstractAuditable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private LocalDate closingDate;

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
    private List<AccountDayBalance> balances;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private AccountCurrentBalance currentBalance;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction topUp(BigDecimal amount, LocalDateTime timestamp) {
        if (this.status != Status.ACTIVE) {
            throw new InactiveAccountException(number);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        var transaction = Transaction.createTopUp(UUID.randomUUID().toString(), amount, this, timestamp);
        currentBalance = currentBalance.addToAvailable(amount, transaction);
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
