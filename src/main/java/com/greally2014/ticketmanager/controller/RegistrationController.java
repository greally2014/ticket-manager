package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.user.RegistrationDto;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private Map<String, String> roles;

    private final CustomUserDetailsService customUserDetailsService;

    public RegistrationController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/showRegistrationForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        model.addAttribute("roles", roles);

        return "register/register";
    }

    @PostMapping("/process")
    public String processRegistration(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
                                      BindingResult bindingResult, Model model,
                                      HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roles);

            return "register/register";

        } else {
            try {
                customUserDetailsService.register(registrationDto, getSiteUrl(request));

                return "register/registration-confirmation";

            } catch (IOException | MessagingException e) {
                e.printStackTrace();
                customUserDetailsService.deleteByUsername(registrationDto);

                return "register/verify-fail";

            }


        }
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (customUserDetailsService.verify(code)) {
            return "register/verify-success";
        } else {
            return "register/verify-fail";
        }
    }

    private String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
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
