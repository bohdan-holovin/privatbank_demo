package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.exception.InsufficientFundsException;
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

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String failureReason;

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

    public static Transaction createTopUp(String uuid, BigDecimal amount, Account toAccount) {
        return Transaction.builder()
                .uuid(uuid)
                .amount(amount)
                .processedAt(null)
                .type(Type.TOP_UP)
                .status(Status.PENDING)
                .toAccount(toAccount)
                .build();
    }

    public void process() {
        this.status = Status.PROCESSING;

        try {
            if (isDebitOperation()) {
                fromAccount.getCurrentBalance().reserveFunds(amount);
            }

            switch (type) {
                case TRANSFER -> {
                    fromAccount.getCurrentBalance().commitDebit(amount, this);
                    toAccount.getCurrentBalance().commitCredit(amount, this);
                }
                case WITHDRAWAL -> fromAccount.getCurrentBalance().commitDebit(amount, this);
                case TOP_UP -> toAccount.getCurrentBalance().commitCredit(amount, this);
            }

            this.status = Status.COMPLETED;
            this.processedAt = LocalDateTime.now();

        } catch (InsufficientFundsException e) {
            this.status = Status.FAILED;
            this.failureReason = "Недостатньо коштів";
        } catch (Exception e) {
            this.status = Status.FAILED;
            this.failureReason = "Помилка обробки: " + e.getMessage();
        }
    }

    private boolean isDebitOperation() {
        return type == Type.TRANSFER || type == Type.WITHDRAWAL;
    }

    public enum Type {
        TOP_UP, TRANSFER, WITHDRAWAL
    }

    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
