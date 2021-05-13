package com.greally2014.ticketmanager.validation.username;

import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final CustomUserDetailsService customUserDetailsService;

    public UniqueUsernameValidator(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.length() < 1 || username.length() > 50) {
            return true; // other annotations detect these conditions
        }
        try {
            customUserDetailsService.loadUserByUsername(username); // username already exists
            return false;

        } catch (UsernameNotFoundException e) {
            return true;
        }
    }
}
