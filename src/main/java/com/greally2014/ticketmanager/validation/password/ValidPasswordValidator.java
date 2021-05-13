package com.greally2014.ticketmanager.validation.password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    /**
     * must be at least 8 characters long, contain a capital letter and special character
     */
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // @NotNull checks for null
        }
        return Pattern.matches(PASSWORD_PATTERN, password);
    }
}
