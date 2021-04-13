package com.greally2014.ticketmanager.config;

import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CustomUserDetailsService customUserDetailsService;

    public CustomAuthenticationSuccessHandler(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(authentication.getName());
        HttpSession session = request.getSession();
        session.setAttribute("userDetails", customUserDetails);

        response.sendRedirect(request.getContextPath() + "/");
    }
}
