package org.holovin.privatbank_demo.app.port.in;

import org.holovin.privatbank_demo.shared.dto.request.user.UserRegistrationRequestDto;
import org.holovin.privatbank_demo.shared.dto.response.user.UserResponseDto;

public interface RegisterUserInPort {

    UserResponseDto execute(UserRegistrationRequestDto request);
}
