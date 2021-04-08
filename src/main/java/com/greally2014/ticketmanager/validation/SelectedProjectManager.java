package com.greally2014.ticketmanager.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = SelectedProjectManagerValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelectedProjectManager {
    String message() default "Must select a project manager";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
