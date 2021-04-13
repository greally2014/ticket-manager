package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.RegistrationDto;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private Map<String, String> roles;

    private final CustomUserDetailsService customUserDetailsService;

    public RegistrationController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showFormForRegistration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        model.addAttribute("roles", roles);

        return "register";
    }

    @PostMapping("/process")
    public String processRegistration(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roles);

            return "register";

        } else {
            customUserDetailsService.register(registrationDto);

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
