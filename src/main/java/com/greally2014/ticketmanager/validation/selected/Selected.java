package com.greally2014.ticketmanager.validation.selected;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = SelectedValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Selected {
    String message() default "Must select a projectCreationDto manager";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
