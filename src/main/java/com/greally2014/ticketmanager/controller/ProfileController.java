package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private final CustomUserDetailsService customUserDetailsService;

    public ProfileController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/showFormForUpdate")
    public String showUpdateProfileForm(Model model) {
        // check if username exists and handle exception / have denied access redirect / error handler
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        try {
            model.addAttribute(
                    "userProfileDto",
                    customUserDetailsService.findProfileDto(principal.getName())
            );

        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            // logout or something
        }

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "profile";
        }

        // check if username exists and handle exception / have denied access redirect / error handler
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        try {
            customUserDetailsService.updateProfile(userProfileDto, principal.getName());

        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            // log the user out or something
        }

        return "index";
    }
}
