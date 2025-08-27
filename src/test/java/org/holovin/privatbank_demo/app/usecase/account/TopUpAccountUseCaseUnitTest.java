package org.holovin.privatbank_demo.app.usecase.account;

import org.holovin.privatbank_demo.app.service.AccountService;
import org.holovin.privatbank_demo.app.service.IdempotencyService;
import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.domain.model.Transaction;
import org.holovin.privatbank_demo.shared.dto.request.account.AccountTopUpRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class TopUpAccountUseCaseUnitTest {

    private IdempotencyService idempotencyService;
    private AccountService accountService;
    private TopUpAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        idempotencyService = mock(IdempotencyService.class);
        accountService = mock(AccountService.class);
        useCase = new TopUpAccountUseCase(idempotencyService, accountService);
    }

    @Test
    void shouldReturnExistingTransactionIfIdempotencyCheckFindsOne() {
        // given
        var request = new AccountTopUpRequestDto("uuid", "12345", BigDecimal.TEN);
        var existingTx = new Transaction();
        existingTx.setStatus(Transaction.Status.COMPLETED);
        existingTx.setType(Transaction.Type.TOP_UP);

        given(idempotencyService.checkOrGetExisting(request.getUuid(), request.getAmount(), Transaction.Type.TOP_UP))
                .willReturn(existingTx);

        // when
        var result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();
        then(idempotencyService).should().checkOrGetExisting(request.getUuid(), request.getAmount(), Transaction.Type.TOP_UP);
        then(accountService).shouldHaveNoInteractions();
    }

    @Test
    void shouldCreateTopUpTransactionIfNoExistingTransaction() {
        // given
        var request = new AccountTopUpRequestDto("uuid", "12345", BigDecimal.TEN);
        var account = mock(Account.class);
        var newTransaction = new Transaction();
        newTransaction.setStatus(Transaction.Status.PENDING);
        newTransaction.setType(Transaction.Type.TOP_UP);

        given(idempotencyService.checkOrGetExisting(request.getUuid(), request.getAmount(), Transaction.Type.TOP_UP))
                .willReturn(null);
        given(accountService.findByNumberWithBalance(request.getAccountNumber())).willReturn(account);
        given(account.topUp(request.getAmount(), request.getUuid())).willReturn(newTransaction);

        // when
        var result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();
        then(idempotencyService).should().checkOrGetExisting(request.getUuid(), request.getAmount(), Transaction.Type.TOP_UP);
        then(accountService).should().findByNumberWithBalance(request.getAccountNumber());
        then(account).should().topUp(request.getAmount(), request.getUuid());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenTopUpFails() {
        // given
        var request = new AccountTopUpRequestDto("uuid", "12345", BigDecimal.TEN);
        var account = mock(Account.class);

        given(idempotencyService.checkOrGetExisting(request.getUuid(), request.getAmount(), Transaction.Type.TOP_UP))
                .willReturn(null);
        given(accountService.findByNumberWithBalance(request.getAccountNumber())).willReturn(account);
        willThrow(new RuntimeException("Something went wrong")).given(account).topUp(request.getAmount(), request.getUuid());

        // when - then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("An unexpected error occurred while processing top-up");

        then(account).should().topUp(request.getAmount(), request.getUuid());
    }
}
