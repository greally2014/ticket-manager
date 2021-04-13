package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/employees")
@PreAuthorize("hasAnyRole('GENERAL_MANAGER', 'PROJECT_MANAGER')")
public class EmployeeController {

    private CustomUserDetailsService customUserDetailsService;

    public EmployeeController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/listAll")
    public String listAllEmployees(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute(
                "employee",
                customUserDetailsService.findAllEmployeesByPrincipalRole(principal)
        );

        return "employee-list";
    }
}
