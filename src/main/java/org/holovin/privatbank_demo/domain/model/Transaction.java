package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.model.base.AbstractAuditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class Transaction extends AbstractAuditable {

    private String uuid;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    public static Transaction createTopUp(String uuid, BigDecimal amount, Account toAccount, LocalDateTime dateTime) {
        return Transaction.builder()
                .uuid(uuid)
                .amount(amount)
                .transactionDate(dateTime)
                .processedAt(dateTime)
                .type(Type.TOP_UP)
                .status(Status.PENDING)
                .toAccount(toAccount)
                .build();
    }

    public enum Type {
        TOP_UP, TRANSFER, WITHDRAWAL
    }

    public enum Status {
        PENDING, COMPLETED, FAILED
    }
}
