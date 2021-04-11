package com.greally2014.ticketmanager.validation.password;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {
    String message() default "Password must contain at least one uppercase letter, digit and a special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
