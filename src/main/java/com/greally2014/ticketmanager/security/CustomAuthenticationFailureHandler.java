package com.greally2014.ticketmanager.security;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final CustomUserDetailsService customUserDetailsService;

    public CustomAuthenticationFailureHandler(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        try {
            User user = customUserDetailsService.loadUserByUsername(username).getUser();
            if (user.isEnabled() && user.isAccountNonLocked()) { //user is eligible for login
                if (user.getFailedAttempt() < CustomUserDetailsService.MAX_FAILED_ATTEMPTS - 1) { // user is not on last attempt
                    customUserDetailsService.increaseFailedAttempts(user);
                    exception = new BadCredentialsException("Invalid username and password.");
                } else {
                    customUserDetailsService.lock(user); // user used last attempt
                    exception = new LockedException("You have used all 3 attempts. " +
                            "Your account has been locked for 24 hours.");
                }
            } else if (!user.isAccountNonLocked()) { // user's account is locked
                if (customUserDetailsService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again");
                } else {
                    exception = new LockedException("Your account is currently locked.");
                }
            } else if (!user.isEnabled()) { // user has not activated account
                exception = new DisabledException("Your account is currently disabled.");
            }

        } catch (UsernameNotFoundException e) {
            exception = new BadCredentialsException("Invalid username and password");
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
