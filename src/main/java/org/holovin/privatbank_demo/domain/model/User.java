package org.holovin.privatbank_demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.holovin.privatbank_demo.domain.model.base.AbstractAuditable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractAuditable {

    private String username;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Account> accounts = new ArrayList<>();

    public static User create(String username, Integer count) {
        var user = new User();
        user.setUsername(username);

        var accounts = IntStream.range(0, count)
                .mapToObj(i -> Account.create())
                .collect(Collectors.toCollection(ArrayList::new));

        user.addAccounts(accounts);
        return user;
    }

    private void addAccounts(List<Account> accounts) {
        this.accounts.addAll(accounts);
        accounts.forEach(account -> account.setUser(this));
    }
}
