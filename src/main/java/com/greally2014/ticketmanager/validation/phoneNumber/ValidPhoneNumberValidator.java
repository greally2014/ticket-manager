package com.greally2014.ticketmanager.validation.phoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidPhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private final String PHONE_NUMBER_PATTERN =
            "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";

    public boolean isValid(String number, ConstraintValidatorContext context) {

        if (number == null || number.length() < 1 || number.length() > 50) {
            return true;
        }

        return Pattern.matches(PHONE_NUMBER_PATTERN, number);
    }
}
