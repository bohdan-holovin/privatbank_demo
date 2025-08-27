package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.UserResponseDto;

public interface UserRegistrationInPort {

    UserResponseDto execute(UserRegistrationRequestDto request);
}
