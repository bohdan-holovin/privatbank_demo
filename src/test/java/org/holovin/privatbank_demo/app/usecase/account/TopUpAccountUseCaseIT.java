package org.holovin.privatbank_demo.app.usecase.account;

import org.holovin.privatbank_demo.app.service.UserService;
import org.holovin.privatbank_demo.domain.model.Account;
import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.shared.dto.request.account.AccountTopUpRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TopUpAccountUseCaseIT {

    @Autowired
    private TopUpAccountUseCase topUpAccountUseCase;

    @Autowired
    private UserService userService;

    private Account account;

    @BeforeEach
    void setUp() {
        var user = User.create("username", 2);
        userService.save(user);
        account = user.getAccounts().getFirst();
    }

    @Test
    void shouldTopUpAccountSuccessfully() {
        // given
        var request = new AccountTopUpRequestDto("uuid", account.getNumber(), BigDecimal.TEN);

        // when
        var response = topUpAccountUseCase.execute(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(response.getType()).isEqualTo("TOP_UP");
    }

    @Test
    void shouldReturnSameTransactionIfIdempotentRequest() {
        // given
        var request = new AccountTopUpRequestDto("uuid2", account.getNumber(), BigDecimal.TEN);
        topUpAccountUseCase.execute(request);

        // when
        var response = topUpAccountUseCase.execute(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
    }
}
