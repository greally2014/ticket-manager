package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.formModel.ProfileUser;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(Model model) {
        model.addAttribute("profileUser", createProfileUser());
        return "profile-form";
    }

    @PostMapping("/saveProfile")
    public String saveProfile(@Valid @ModelAttribute("profileUser") ProfileUser profileUser,
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            logger.info("=====> Form error(s): " + errors);

            return "profile-form";
        }

        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        if (customUserDetailsService.isUsernameTaken(profileUser.getUsername()) &&
                !profileUser.getUsername().equals(principal.getName())) {
            logger.info("=====> Username taken: " + profileUser.getUsername());

            model.addAttribute("profileUser", createProfileUser());
            model.addAttribute("registrationError", "Username already exists.");
            return "profile-form";

        } else {
            logger.info("=====> Updating profile details: " + profileUser.getUsername());

            customUserDetailsService.updateProfileDetails(profileUser, principal.getName());
            return "index";
        }
    }

    public ProfileUser createProfileUser() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomUserDetails) customUserDetailsService.loadUserByUsername(principal.getName())).getUser();
        return new ProfileUser(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
