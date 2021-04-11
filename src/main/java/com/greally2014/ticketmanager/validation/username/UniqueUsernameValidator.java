package com.greally2014.ticketmanager.validation.username;

import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.length() < 1 || username.length() > 50) {
            return true;
        }
        try {
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(username);
            return false;

        } catch (UsernameNotFoundException e) {
            return true;
        }
    }
}
