package org.holovin.privatbank_demo.app.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
