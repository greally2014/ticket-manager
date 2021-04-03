package com.greally2014.ticketmanager.validation;

import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.Principal;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String > {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
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
