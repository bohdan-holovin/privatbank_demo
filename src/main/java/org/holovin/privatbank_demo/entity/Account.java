package org.holovin.privatbank_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
    name = "accounts",
    indexes = {
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_account_number", columnList = "account_number")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @Column(name = "account_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode = "UAH";

    @Column(name = "status", length = 20)
    private String status = "active";

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> incomingTransactions;

    @OneToMany(mappedBy = "account")
    private List<AccountBalance> balances;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private CurrentBalance currentBalance;
}
