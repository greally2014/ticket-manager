package com.greally2014.ticketmanager.validation;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String > {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean valid = true;
        try {
            UserDetails user = userDetailsService.loadUserByEmail(email);
            valid = false;
        } catch (EmailNotFoundException exception) {
            // nothing
        }
        return valid;
    }
}
