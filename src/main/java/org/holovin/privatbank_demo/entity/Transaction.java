package org.holovin.privatbank_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_from_account_date", columnList = "from_account_id, transaction_date"),
        @Index(name = "idx_to_account_date", columnList = "to_account_id, transaction_date"),
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_transaction_uuid", columnList = "transaction_uuid"),
        @Index(name = "idx_reference_number", columnList = "reference_number")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @Column(name = "transaction_uuid", nullable = false, unique = true, length = 36)
    private String transactionUuid;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode = "UAH";

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType;

    @Column(name = "transaction_subtype", length = 50)
    private String transactionSubtype;

    @Column(name = "status", length = 20)
    private String status = "pending";

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "reference_number", length = 50)
    private String referenceNumber;

    @Column(name = "merchant_info", columnDefinition = "JSON")
    private String merchantInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;
}
