package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.formModel.ProfileUser;
import com.greally2014.ticketmanager.formModel.RegistrationUser;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private CustomUserDetailsService userDetailsService;

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
            logger.info("=====> Form error(s)");
            return "profile-form";
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(profileUser.getUserName());
            Principal principal = SecurityContextHolder.getContext().getAuthentication();
            if (!profileUser.getUserName().equals(principal.getName())) {
                logger.info("=====> Username taken: " + profileUser.getUserName());
                model.addAttribute("profileUser", createProfileUser());
                model.addAttribute("registrationError", "Username already exists.");
            } else {
                logger.info("=====> Updating profile details: " + profileUser.getUserName());
                userDetailsService.updateProfileDetails(profileUser, principal.getName());
                logger.info("=====> Profile updated successfully");
                model.addAttribute("formUser", profileUser);
            }
            return "profile-form";

        } catch (UsernameNotFoundException exception) {
            logger.info("=====> Updating profile details: " + profileUser.getUserName());
            Principal principal = SecurityContextHolder.getContext().getAuthentication();
            userDetailsService.updateProfileDetails(profileUser, principal.getName());
            logger.info("=====> Profile updated successfully");
            model.addAttribute("formUser", profileUser);
            return "profile-form";
        }
    }

    public ProfileUser createProfileUser() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomUserDetails) userDetailsService.loadUserByUsername(principal.getName())).getUser();
        ProfileUser profileUser = new ProfileUser();

        profileUser.setId(user.getId());
        profileUser.setUserName(user.getUserName());
        profileUser.setFirstName(user.getFirstName());
        profileUser.setLastName(user.getLastName());
        profileUser.setEmail(user.getEmail());

        return profileUser;
    }
}
