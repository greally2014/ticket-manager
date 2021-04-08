package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.formModel.ProfileFormUser;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
public class ProfileController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("profileFormUser",
                customUserDetailsService.getProfileFormUser(principal.getName()));
        return "profile-form";
    }

    @PostMapping("/save")
    public String saveProfile(@Valid @ModelAttribute("profileFormUser") ProfileFormUser profileFormUser,
                              Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "profile-form";
        }

        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        if (customUserDetailsService.isUsernameTaken(profileFormUser.getUsername()) &&
                !profileFormUser.getUsername().equals(principal.getName())) {
            model.addAttribute("profileFormUser",
                    customUserDetailsService.getProfileFormUser(principal.getName()));
            model.addAttribute("profileUpdateError", "Username already exists.");
            return "profile-form";

        } else {
            customUserDetailsService.updateProfile(profileFormUser, principal.getName());
            return "index";
        }
    }
}
