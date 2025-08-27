package org.holovin.privatbank_demo.infra.db.adapter;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.out.UserOutPort;
import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.infra.db.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserOutPort {

    private final UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
