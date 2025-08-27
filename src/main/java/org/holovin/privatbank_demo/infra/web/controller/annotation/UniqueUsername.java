package org.holovin.privatbank_demo.infra.web.controller.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.holovin.privatbank_demo.infra.web.controller.validator.UniqueUsernameValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {

    String message() default "Username already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
