package org.holovin.privatbank_demo.app.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.domain.model.User;
import org.holovin.privatbank_demo.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }
}
