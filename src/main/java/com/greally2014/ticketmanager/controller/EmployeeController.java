package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/employees")
@PreAuthorize("hasAnyRole('GENERAL_MANAGER', 'PROJECT_MANAGER')")
public class EmployeeController {

    private final CustomUserDetailsService customUserDetailsService;

    public EmployeeController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/listAll")
    public String listAllEmployees(Model model, Principal principal) {
        model.addAttribute(
                "employees",
                customUserDetailsService.findAllEmployees(principal)
        );

        return "employee/employee-list";
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String deleteEmployee(@RequestParam("id") Long id, SessionRegistry sessionRegistry) {
        try {
            User user = customUserDetailsService.delete(id);

            Object deletedPrincipal = null;
            for (Object principalObject : sessionRegistry.getAllPrincipals()) {
                Principal principal = (Principal) principalObject;
                if (principal.getName().equals(user.getUsername())) {
                    deletedPrincipal = principalObject;
                }
            }
            List<SessionInformation> activeSessions = sessionRegistry.getAllSessions(deletedPrincipal, true);
            for (SessionInformation session : activeSessions) {
                session.expireNow();;
                sessionRegistry.removeSessionInformation((session.getSessionId()));
            }

        } catch (UserNotFoundException e) {
            // nothing
        }

        return "redirect:/employees/listAll";
    }

    @GetMapping("/showDetailsPage")
    public String showEmployeeDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("employee", customUserDetailsService.findById(id));

            return "employee/employee-details";

        } catch (UserNotFoundException e) {

            return "redirect:/employees/listAll";
        }
    }

}
