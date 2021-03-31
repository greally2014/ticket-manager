package com.greally2014.ticketmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/projects")
    public String showProjects() {
        return "projects";
    }
}
