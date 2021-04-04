package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.ProjectManager;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.formModel.FormProject;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.service.ProjectManagerService;
import com.greally2014.ticketmanager.service.ProjectService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ProjectManagerService projectManagerService;

    @GetMapping("/listProjects")
    public String listProjects(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        List<Project> projects = projectService.findAllByUserAndUserRole(principal.getName());
        model.addAttribute("formProjects", projects);
        return "project-list";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        FormProject formProject = new FormProject();
        formProject.setDateCreated(LocalDate.now());
        List<ProjectManager> projectManagers = projectManagerService.findAllOrderByUsername();
        model.addAttribute("formProject", formProject);
        model.addAttribute("projectManagers", projectManagers);
        return "project-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("projectId") Long projectId, Model model) {
        try {
            model.addAttribute("formProject", projectFormProject(projectId));
            return "project-form";

        } catch (ProjectNotFoundException exception) {
            exception.printStackTrace();
            return "project-list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("formProject") @Valid Project project,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            logger.info("=====> Form error(s): " + errors);

            return "project-form";

        } else {
            projectService.save(project);
            return "redirect:/projects/listProjects";
        }
    }

    @GetMapping("/delete")
    public String deleteProject(@ModelAttribute("projectId") Long projectId) {
        projectService.deleteById(projectId);
        return "redirect:/projects/listProjects";
    }

    public FormProject projectFormProject(Long projectId) throws ProjectNotFoundException {
        Project project = projectService.findById(projectId);
        FormProject formProject = new FormProject(
                project.getId(),
                project.getTitle(),
                project.getDescription()
        );
        formProject.setDateCreated(project.getDateCreated());
        return formProject;
    }
}
