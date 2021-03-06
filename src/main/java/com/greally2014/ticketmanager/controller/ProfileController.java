package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    /**
     * strips whitespace from form entries, converting them to null entries if pure whitespace.
     * pure whitespace entries are then flagged by the @NotNull annotation
     */
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
    public String showUpdateProfileForm(Model model, Principal principal) {
        model.addAttribute(
                "userProfileDto",
                customUserDetailsService.findProfileDto(principal.getName())
        );
        return "profile/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
                                BindingResult bindingResult, Principal principal) throws IOException {
        if (bindingResult.hasErrors()) {
            return "profile/profile";
        }
        customUserDetailsService.updateProfile(userProfileDto, principal.getName());
        return "redirect:/"; // redirects to homepage based on the user role

    }
}
