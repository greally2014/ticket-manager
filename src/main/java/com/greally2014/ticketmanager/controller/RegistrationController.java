package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.user.RegistrationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private Logger logger = Logger.getLogger(getClass().getName());

    private Map<String, String> roles;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationUser", new RegistrationUser());
        model.addAttribute("roles", roles);
        return "registration-form";
    }

    @PostMapping("/processRegistration")
    public String processRegistration(@Valid @ModelAttribute("registrationUser") RegistrationUser registrationUser,
                                      BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            logger.info("=====> Form error(s)");
            model.addAttribute("roles", roles);
            return "registration-form";
        }

        try {
            UserDetails existingUser = userDetailsService.loadUserByUsername(registrationUser.getUserName());
            logger.info("=====> Username taken: " + registrationUser.getUserName() +
                    " == " + existingUser.getUsername());

            model.addAttribute("registrationUser", new RegistrationUser());
            model.addAttribute("roles", roles);
            model.addAttribute("registrationError", "Username already exists.");
            return "registration-form";

        } catch (UsernameNotFoundException exception) {
            logger.info("=====> Saving user: " + registrationUser.getUserName());
            userDetailsService.save(registrationUser);
            logger.info("=====> User saved successfully");
            return "registration-confirmation";
        }
    }

    @PostConstruct
    public void loadRoles() {
        roles = new LinkedHashMap<>();
        roles.put("ROLE_GENERAL_MANAGER", "General Manager");
        roles.put("ROLE_PROJECT_MANAGER", "Project Manager");
        roles.put("ROLE_DEVELOPER", "Developer");
        roles.put("ROLE_SUBMITTER", "Submitter");
    }

}
