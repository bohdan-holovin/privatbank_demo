package org.holovin.privatbank_demo.app.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.port.out.UserOutPort;
import org.holovin.privatbank_demo.domain.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserOutPort userOutPort;

    public void save(User user) {
        userOutPort.save(user);
    }

    public boolean existsByUsername(String username) {
        return userOutPort.existsByUsername(username);
    }
}
