package org.holovin.privatbank_demo.domain.model;

import org.holovin.privatbank_demo.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentBalanceUnitTest {

    private CurrentBalance balance;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        balance = CurrentBalance.create();
        transaction = new Transaction();
    }

    @Test
    void shouldInitializeWithZeroBalancesWhenCreated() {
        // when
        var currentBalance = CurrentBalance.create();

        // then
        assertThat(currentBalance.getAvailableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(currentBalance.getPendingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(currentBalance.getLastTransaction()).isNull();
    }

    @Test
    void shouldReserveFundsSuccessfullyWhenSufficientBalance() {
        // given
        balance.setAvailableBalance(BigDecimal.valueOf(100));
        balance.setPendingBalance(BigDecimal.valueOf(10));
        var amountToReserve = BigDecimal.valueOf(50);

        // when
        balance.reserveFunds(amountToReserve);

        // then
        assertThat(balance.getAvailableBalance()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(balance.getPendingBalance()).isEqualByComparingTo(BigDecimal.valueOf(60));
    }

    @Test
    void shouldThrowExceptionWhenReservingMoreThanAvailable() {
        // given
        balance.setAvailableBalance(BigDecimal.valueOf(30));
        var amountToReserve = BigDecimal.valueOf(50);

        // when - then
        assertThatThrownBy(() -> balance.reserveFunds(amountToReserve))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Not enough funds to reserve");
    }

    @Test
    void shouldCommitCreditSuccessfully() {
        // given
        balance.setAvailableBalance(BigDecimal.valueOf(20));
        balance.setPendingBalance(BigDecimal.valueOf(30));
        var amountToCredit = BigDecimal.valueOf(25);

        // when
        balance.commitCredit(amountToCredit, transaction);

        // then
        assertThat(balance.getAvailableBalance()).isEqualByComparingTo(BigDecimal.valueOf(45));
        assertThat(balance.getPendingBalance()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(balance.getLastTransaction()).isEqualTo(transaction);
    }

    @Test
    void shouldNotSetNegativePendingBalanceWhenCommitCreditExceedsPending() {
        // given
        balance.setAvailableBalance(BigDecimal.valueOf(10));
        balance.setPendingBalance(BigDecimal.valueOf(5));
        var amountToCredit = BigDecimal.valueOf(10);

        // when
        balance.commitCredit(amountToCredit, transaction);

        // then
        assertThat(balance.getAvailableBalance()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(balance.getPendingBalance()).isEqualByComparingTo(BigDecimal.ZERO); // max(BigDecimal.ZERO)
        assertThat(balance.getLastTransaction()).isEqualTo(transaction);
    }

    @Test
    void shouldCommitDebitSuccessfully() {
        // given
        balance.setPendingBalance(BigDecimal.valueOf(50));
        var amountToDebit = BigDecimal.valueOf(20);

        // when
        balance.commitDebit(amountToDebit, transaction);

        // then
        assertThat(balance.getPendingBalance()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(balance.getLastTransaction()).isEqualTo(transaction);
    }
}
