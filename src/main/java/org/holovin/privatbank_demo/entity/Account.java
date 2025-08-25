package org.holovin.privatbank_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractAuditable {

    @Column(nullable = false, unique = true, length = 20)
    private String number;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private LocalDate closingDate;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> incomingTransactions;

    @OneToMany(mappedBy = "account")
    private List<AccountDayBalance> balances;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private AccountCurrentBalance accountCurrentBalance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum Status {
        ACTIVE,
        CLOSED,
    }
}
