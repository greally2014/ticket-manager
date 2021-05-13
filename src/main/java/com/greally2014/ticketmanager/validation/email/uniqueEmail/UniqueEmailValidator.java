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
            return true; // other annotations detect these conditions
        }
        boolean valid;

        try {
            // email already exists
            CustomUserDetails customUserdetails = customUserDetailsService.loadUserByEmail(email);
            try {
                // user is currently logged in
                Principal principal = SecurityContextHolder.getContext().getAuthentication();

                // valid if the user with the taken email is the current user
                valid = customUserdetails.getUsername().equals(principal.getName());

            } catch (NullPointerException exception) {
                /**
                 * user is registering
                 * not valid because the email is taken by a different user
                 * because the current user is registering and does not have an account
                 */
                valid = false;
            }
        } catch (EmailNotFoundException exception) {
            valid = true; // valid because email does not exist
        }
        return valid;
    }
}
