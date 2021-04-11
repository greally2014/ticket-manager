package com.greally2014.ticketmanager.validation.phoneNumber;

import org.springframework.stereotype.Repository;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidPhoneNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPhoneNumber {
    String message() default "Please enter a valid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

