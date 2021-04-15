package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employees")
@PreAuthorize("hasAnyRole('GENERAL_MANAGER', 'PROJECT_MANAGER')")
public class EmployeeController {

    private final CustomUserDetailsService customUserDetailsService;

    public EmployeeController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/listAll")
    public String listAllEmployees(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute(
                "employees",
                customUserDetailsService.findAllEmployeesByPrincipalRoleOrderByUsername(authentication)
        );

        return "employee-list";
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String deleteEmployee(@RequestParam("id") Long id) {
        customUserDetailsService.delete(id);
        return "redirect:/employees/listAll";
    }

    @GetMapping("/showDetailsPage")
    public String showEmployeeDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("employee", customUserDetailsService.findById(id));

            return "employee-details";

        } catch (UserNotFoundException e) {
            e.printStackTrace();

            return "redirect:/employees/listAll";
        }
    }
}
