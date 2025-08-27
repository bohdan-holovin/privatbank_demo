package org.holovin.privatbank_demo.domain.model;

import org.holovin.privatbank_demo.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class TransactionUnitTest {

    private Account fromAccount;
    private Account toAccount;
    private CurrentBalance fromBalance;
    private CurrentBalance toBalance;

    @BeforeEach
    void setUp() {
        fromAccount = mock(Account.class);
        toAccount = mock(Account.class);
        fromBalance = mock(CurrentBalance.class);
        toBalance = mock(CurrentBalance.class);

        given(fromAccount.getCurrentBalance()).willReturn(fromBalance);
        given(toAccount.getCurrentBalance()).willReturn(toBalance);
    }

    @Test
    void shouldInitializeTopUpCorrectlyWhenCreateTopUp() {
        // given
        var uuid = "uuid";
        var amount = BigDecimal.TEN;

        // when
        var tx = Transaction.createTopUp(uuid, amount, toAccount);

        // then
        assertThat(tx.getUuid()).isEqualTo(uuid);
        assertThat(tx.getAmount()).isEqualByComparingTo(amount);
        assertThat(tx.getType()).isEqualTo(Transaction.Type.TOP_UP);
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.PENDING);
        assertThat(tx.getToAccount()).isEqualTo(toAccount);
        assertThat(tx.getFromAccount()).isNull();
        assertThat(tx.getProcessedAt()).isNull();
    }

    @Test
    void shouldInitializeTransferCorrectlyWhenCreateTransfer() {
        // given
        var uuid = "uuid";
        var amount = BigDecimal.ONE;

        // when
        var tx = Transaction.createTransfer(uuid, amount, fromAccount, toAccount);

        // then
        assertThat(tx.getUuid()).isEqualTo(uuid);
        assertThat(tx.getAmount()).isEqualByComparingTo(amount);
        assertThat(tx.getType()).isEqualTo(Transaction.Type.TRANSFER);
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.PENDING);
        assertThat(tx.getFromAccount()).isEqualTo(fromAccount);
        assertThat(tx.getToAccount()).isEqualTo(toAccount);
        assertThat(tx.getProcessedAt()).isNull();
    }

    @Test
    void shouldInitializeWithdrawCorrectlyWhenCreateWithdraw() {
        // given
        var uuid = "uuid";
        var amount = BigDecimal.valueOf(50);

        // when
        var tx = Transaction.createWithdraw(uuid, amount, fromAccount);

        // then
        assertThat(tx.getUuid()).isEqualTo(uuid);
        assertThat(tx.getAmount()).isEqualByComparingTo(amount);
        assertThat(tx.getType()).isEqualTo(Transaction.Type.WITHDRAW);
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.PENDING);
        assertThat(tx.getFromAccount()).isEqualTo(fromAccount);
        assertThat(tx.getToAccount()).isNull();
        assertThat(tx.getProcessedAt()).isNull();
    }

    @Test
    void shouldCompleteTransactionSuccessfullyWhenProcessTopUp() {
        // given
        var tx = Transaction.createTopUp("uuid", BigDecimal.TEN, toAccount);

        // when
        tx.process();

        // then
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(tx.getProcessedAt()).isNotNull();

        then(toBalance).should().commitCredit(BigDecimal.TEN, tx);
    }


    @Test
    void shouldCompleteTransactionSuccessfullyWhenProcessTransfer() {
        // given
        var tx = Transaction.createTransfer("uuid", BigDecimal.TEN, fromAccount, toAccount);

        // when
        tx.process();

        // then
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(tx.getProcessedAt()).isNotNull();

        then(fromBalance).should().reserveFunds(BigDecimal.TEN);
        then(fromBalance).should().commitDebit(BigDecimal.TEN, tx);
        then(toBalance).should().commitCredit(BigDecimal.TEN, tx);
    }

    @Test
    void shouldCompleteTransactionSuccessfullyWhenProcessWithdraw() {
        // given
        var tx = Transaction.createWithdraw("uuid", BigDecimal.TEN, fromAccount);

        // when
        tx.process();

        // then
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(tx.getProcessedAt()).isNotNull();

        then(fromBalance).should().reserveFunds(BigDecimal.TEN);
        then(fromBalance).should().commitDebit(BigDecimal.TEN, tx);
    }

    @Test
    void shouldFailTransactionWhenInsufficientFundsDuringProcess() {
        // given
        var tx = Transaction.createWithdraw("uuid", BigDecimal.TEN, fromAccount);
        willThrow(new InsufficientFundsException("Not enough")).given(fromBalance).reserveFunds(BigDecimal.TEN);

        // when
        tx.process();

        // then
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.FAILED);
        assertThat(tx.getFailureReason()).isEqualTo("Insufficient funds");
        assertThat(tx.getProcessedAt()).isNull();
    }

    @Test
    void shouldFailTransactionWhenUnexpectedExceptionDuringProcess() {
        // given
        var tx = Transaction.createTransfer("uuid", BigDecimal.ONE, fromAccount, toAccount);
        willThrow(new RuntimeException("Something")).given(fromBalance).reserveFunds(BigDecimal.ONE);

        // when
        tx.process();

        // then
        assertThat(tx.getStatus()).isEqualTo(Transaction.Status.FAILED);
        assertThat(tx.getFailureReason()).startsWith("Processing error: ");
        assertThat(tx.getProcessedAt()).isNull();
    }

    @Test
    void shouldReturnTrueWhenSameOperationWithMatchingAmountAndType() {
        // given
        var tx = Transaction.createTopUp("uuid", BigDecimal.valueOf(100), toAccount);

        // when
        boolean result = tx.isSameOperation(BigDecimal.valueOf(100), Transaction.Type.TOP_UP);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenSameOperationWithDifferentAmountOrType() {
        // given
        var tx = Transaction.createWithdraw("uuid", BigDecimal.valueOf(100), fromAccount);

        // when - then
        assertThat(tx.isSameOperation(BigDecimal.valueOf(50), Transaction.Type.WITHDRAW)).isFalse();
        assertThat(tx.isSameOperation(BigDecimal.valueOf(100), Transaction.Type.TRANSFER)).isFalse();
    }
}
