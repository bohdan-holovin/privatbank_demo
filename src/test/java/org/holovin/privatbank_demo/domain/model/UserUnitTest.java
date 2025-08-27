package org.holovin.privatbank_demo.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUnitTest {

    @Test
    void shouldCreateUserWithGivenUsernameAndAccounts() {
        // given
        var username = "user";
        var accountCount = 3;

        // when
        var user = User.create(username, accountCount);

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getAccounts()).hasSize(accountCount);

        user.getAccounts()
                .forEach(account -> assertThat(account.getUser()).isEqualTo(user));
    }
}
