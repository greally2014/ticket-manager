package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.formModel.RegistrationFormUser;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private Logger logger = Logger.getLogger(getClass().getName());

    private Map<String, String> roles;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationFormUser", new RegistrationFormUser());
        model.addAttribute("roles", roles);
        return "registration-form";
    }

    @PostMapping("/processRegistration")
    public String processRegistration(@Valid @ModelAttribute("registrationFormUser") RegistrationFormUser registrationFormUser,
                                      BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            logger.info("=====> Form error(s): " + errors);

            model.addAttribute("roles", roles);
            return "registration-form";
        }

        if (customUserDetailsService.isUsernameTaken(registrationFormUser.getUsername())) {
            logger.info("=====> Username taken: " + registrationFormUser.getUsername());

            model.addAttribute("registrationFormUser", new RegistrationFormUser());
            model.addAttribute("roles", roles);
            model.addAttribute("registrationError", "Username already exists.");
            return "registration-form";

        } else {
            logger.info("=====> Saving user: " + registrationFormUser.getUsername());

            customUserDetailsService.save(registrationFormUser);
            return "registration-confirmation";
        }
    }

    @PostConstruct
    public void setRoles() {
        roles = new LinkedHashMap<>();
        roles.put("ROLE_GENERAL_MANAGER", "General Manager");
        roles.put("ROLE_PROJECT_MANAGER", "Project Manager");
        roles.put("ROLE_DEVELOPER", "Developer");
        roles.put("ROLE_SUBMITTER", "Submitter");
    }

}
