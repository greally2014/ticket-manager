package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping("/")
    public String showMainPage(Model model) {
        return "index";
    }

    @GetMapping("/projects")
    public String showProjects() {
        return "project-list";
    }
}
