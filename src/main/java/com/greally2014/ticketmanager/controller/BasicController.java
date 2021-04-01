package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class BasicController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping("/")
    public String showMainPage(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(principal.getName());
        Long userId = customUserDetails.getUser().getId();
        model.addAttribute("user_id", userId);
        return "index";
    }

    @GetMapping("/projects")
    public String showProjects() {
        return "projects";
    }
}
