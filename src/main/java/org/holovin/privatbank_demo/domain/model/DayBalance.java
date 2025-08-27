package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.model.base.AbstractAuditable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "day_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DayBalance extends AbstractAuditable {

    private LocalDate balanceDate;
    private BigDecimal availableBalance;
    private BigDecimal pendingBalance;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
