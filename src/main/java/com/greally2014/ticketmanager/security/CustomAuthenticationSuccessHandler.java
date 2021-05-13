package com.greally2014.ticketmanager.security;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.utility.Util;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final CustomUserDetailsService customUserDetailsService;

    public CustomAuthenticationSuccessHandler(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(authentication.getName());

        User user = customUserDetails.getUser();
        resetFailedAttempts(user); // resets failed attempts to 0

        handle(request, response, authentication); // redirects users after login based on their role
        clearAuthenticationAttributes(request);

        super.onAuthenticationSuccess(request, response, authentication);
    }


    protected void resetFailedAttempts(User user) {
        if (user.getFailedAttempt() > 0) {
            customUserDetailsService.resetFailedAttempts(user.getEmail());
        }
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication) throws IOException {
        String targetUrl = Util.determineTargetUrl(authentication); // determines the redirect page based on user role

        if (response.isCommitted()) {
            logger.info("Response already committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
