package com.greally2014.ticketmanager.validation;

import com.greally2014.ticketmanager.entity.User;
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
    private CustomUserDetailsService userDetailsService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean valid = true;
        try {
            CustomUserDetails customUserdetails = (CustomUserDetails) userDetailsService.loadUserByEmail(email);
            try {
                Principal principal = SecurityContextHolder.getContext().getAuthentication();
                if (principal == null) {
                    throw new NullPointerException();
                } else {
                    if (!customUserdetails.getUsername().equals(principal.getName())) {
                        valid = false;
                    }
                }
            } catch (NullPointerException exception) {
                valid = false;
            }
        } catch (EmailNotFoundException exception) {
            // nothing
        }
        return valid;
    }
}
