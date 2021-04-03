package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String deleteProject(@ModelAttribute("projectId") Long projectId) {
        projectService.deleteById(projectId);
        return "redirect:/projects/listProjects";
    }



    @GetMapping("/showFormForUpdate")
    public String updateProject(@RequestParam("projectId") Long projectId, Model model) {
        try {
            Project project = projectService.findById(projectId);
            model.addAttribute("project", project);
            return "project-form";

        } catch (ProjectNotFoundException exception) {
            exception.printStackTrace();
        }
        return "redirect:/projects/listProjects";
    }
}
