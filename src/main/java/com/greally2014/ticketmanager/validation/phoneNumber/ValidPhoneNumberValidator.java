package com.greally2014.ticketmanager.validation.phoneNumber;

import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.Principal;
import java.util.regex.Pattern;

public class ValidPhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private String message;

    private CustomUserDetailsService customUserDetailsService;

    private UserRepository userRepository;

    public ValidPhoneNumberValidator(CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(ValidPhoneNumber constraint) {
        message = constraint.message();
    }

    private final String PHONE_NUMBER_PATTERN =
            "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";

    public boolean isValid(String number, ConstraintValidatorContext context) {

        if (number == null || number.length() < 1 || number.length() > 50) {
            return true;
        }

        if (!Pattern.matches(PHONE_NUMBER_PATTERN, number)) {
            return false;
        };

        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        if (userRepository.findAll().stream().map(User::getPhoneNumber).anyMatch(o -> o.equals(number))) {
            if (principal == null || !customUserDetailsService.loadUserByUsername(principal.getName())
                    .getUser().getPhoneNumber().equals(number)) {
                message = "Phone number already in use.";
                context.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();

                return false;
            }

            return true;
        }

        return true;
    }
}
