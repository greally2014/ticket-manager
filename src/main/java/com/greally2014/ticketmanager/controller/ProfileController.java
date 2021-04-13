package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
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

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute(
                "userProfileDto",
                customUserDetailsService.getProfileDto(principal.getName())
        );

        return "profile";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "profile";
        }

        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        customUserDetailsService.updateProfile(userProfileDto, principal.getName());

        return "index";
    }
}
