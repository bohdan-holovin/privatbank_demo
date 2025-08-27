package org.holovin.privatbank_demo.infra.web.controller.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.UserService;
import org.holovin.privatbank_demo.infra.web.controller.annotation.UniqueUsername;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return !userService.existsByUsername(value);
    }
}
