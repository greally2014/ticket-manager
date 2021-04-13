package com.greally2014.ticketmanager.validation.email.uniqueEmail;

import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.Principal;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String > {

    private final CustomUserDetailsService customUserDetailsService;

    public UniqueEmailValidator(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.length() < 1 || email.length() > 50) {
            return true;
        }
        boolean valid;

        try {
            CustomUserDetails customUserdetails = customUserDetailsService.loadUserByEmail(email);
            try {
                Principal principal = SecurityContextHolder.getContext().getAuthentication();
                valid = customUserdetails.getUsername().equals(principal.getName());

            } catch (NullPointerException exception) {
                valid = false;
            }
        } catch (EmailNotFoundException exception) {
            valid = true;
        }
        return valid;
    }
}
