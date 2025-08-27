package org.holovin.privatbank_demo.app.port.out;

import org.holovin.privatbank_demo.domain.model.User;

public interface UserOutPort {

    void save(User user);

    boolean existsByUsername(String username);
}
