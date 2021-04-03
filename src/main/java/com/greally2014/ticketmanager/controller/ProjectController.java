package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/listProjects")
    public String listProjects(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        List<Project> projects = projectService.findAllByUserAndUserRole(principal.getName());
        model.addAttribute("projects", projects);
        return "project-list";
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String deleteProject(@RequestParam("projectId") Long projectId) {
        projectService.deleteById(projectId);
        return "redirect:/projects/listProjects";
    }
}
