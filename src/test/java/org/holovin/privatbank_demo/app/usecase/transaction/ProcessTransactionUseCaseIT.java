package org.holovin.privatbank_demo.app.usecase.transaction;

import jakarta.persistence.EntityManager;
import org.holovin.privatbank_demo.app.service.TransactionService;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProcessTransactionUseCaseIT {

    @Autowired
    private ProcessTransactionUseCase processTransactionUseCase;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldExecuteIsPersisted() {
        // given
        var user = User.create("test-user", 1);
        entityManager.persist(user);

        var account = user.getAccounts().getFirst();

        var transaction = account.topUp(BigDecimal.TEN, "uuid1231");
        entityManager.persist(transaction);
        entityManager.flush();
        entityManager.clear();

        // when
        processTransactionUseCase.execute(transaction);

        // then
        var transactionFromDb = transactionService.findById(transaction.getId());
        assertThat(transactionFromDb.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        // when - then
        assertThatThrownBy(() -> processTransactionUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("transaction must be persisted");
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNotPersisted() {
        // given
        var tx = new Transaction();
        tx.setType(Transaction.Type.TOP_UP);

        // when - then
        assertThatThrownBy(() -> processTransactionUseCase.execute(tx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("transaction must be persisted");
    }

    @Test
    void shouldCompleteTopUpTransaction() {
        // given
        var user = User.create("topup-user", 1);
        entityManager.persist(user);
        var account = user.getAccounts().getFirst();

        var tx = account.topUp(BigDecimal.valueOf(100), "uuid-topup");
        entityManager.persist(tx);
        entityManager.flush();
        entityManager.clear();

        // when
        processTransactionUseCase.execute(tx);

        // then
        var transactionFromDb = transactionService.findById(tx.getId());
        assertThat(transactionFromDb.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(transactionFromDb.getToAccount().getCurrentBalance().getAvailableBalance()).isEqualByComparingTo("100");
    }

    @Test
    void shouldCompleteTransferTransaction() {
        // given
        var user = User.create("transfer-user", 2);
        entityManager.persist(user);

        var fromAccount = user.getAccounts().get(0);
        var toAccount = user.getAccounts().get(1);

        var topUp = fromAccount.topUp(BigDecimal.valueOf(200), "uuid-topup-transfer");
        entityManager.persist(topUp);
        processTransactionUseCase.execute(topUp);

        var transfer = fromAccount.transferTo(toAccount, BigDecimal.valueOf(150), "uuid-transfer");
        entityManager.persist(transfer);
        entityManager.flush();
        entityManager.clear();

        // when
        processTransactionUseCase.execute(transfer);

        // then
        var transactionFromDb = transactionService.findById(transfer.getId());
        assertThat(transactionFromDb.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(transactionFromDb.getFromAccount().getCurrentBalance().getAvailableBalance()).isEqualByComparingTo("50");
        assertThat(transactionFromDb.getToAccount().getCurrentBalance().getAvailableBalance()).isEqualByComparingTo("150");
    }

    @Test
    void shouldCompleteWithdrawTransaction() {
        // given
        var user = User.create("withdraw-user", 1);
        entityManager.persist(user);

        var account = user.getAccounts().getFirst();

        var topUp = account.topUp(BigDecimal.valueOf(300), "uuid-topup-withdraw");
        entityManager.persist(topUp);
        processTransactionUseCase.execute(topUp);

        var withdraw = account.withdraw(BigDecimal.valueOf(120), "uuid-withdraw");
        entityManager.persist(withdraw);
        entityManager.flush();
        entityManager.clear();

        // when
        processTransactionUseCase.execute(withdraw);

        // then
        var transactionFromDb = transactionService.findById(withdraw.getId());
        assertThat(transactionFromDb.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(transactionFromDb.getFromAccount().getCurrentBalance().getAvailableBalance()).isEqualByComparingTo("180");
    }

    @Test
    void shouldFailWhenInsufficientFunds() {
        // given
        var user = User.create("fail-user", 2);
        entityManager.persist(user);

        var fromAccount = user.getAccounts().get(0);
        var toAccount = user.getAccounts().get(1);

        var transfer = Transaction.createTransfer("uuid-fail", BigDecimal.valueOf(50), fromAccount, toAccount);
        entityManager.persist(transfer);
        entityManager.flush();
        entityManager.clear();

        // when
        processTransactionUseCase.execute(transfer);

        // then
        var transactionFromDb = transactionService.findById(transfer.getId());
        assertThat(transactionFromDb.getStatus()).isEqualTo(Transaction.Status.FAILED);
        assertThat(transactionFromDb.getFailureReason()).isEqualTo("Insufficient funds");
    }
}
