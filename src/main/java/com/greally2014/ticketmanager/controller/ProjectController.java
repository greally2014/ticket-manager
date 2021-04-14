package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.ProjectDto;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.service.ProjectManagerService;
import com.greally2014.ticketmanager.service.ProjectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectManagerService projectManagerService;

    public ProjectController(ProjectService projectService, ProjectManagerService projectManagerService) {
        this.projectService = projectService;
        this.projectManagerService = projectManagerService;
    }

    @GetMapping("/listAll")
    public String listAllProjects(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("projects",
                projectService.findAllByUsername(principal.getName())
        );

        return "project-list";

    }

    @GetMapping("/showAddForm")
    public String showAddProjectForm(Model model) {
        model.addAttribute("projectCreationDto",
                new ProjectCreationDto(
                        new ProjectDto(),
                        projectManagerService.findProfileDtoList()
                )
        );

        return "project-create";
    }

    @GetMapping("/showUpdateFieldsForm")
    public String showUpdateProjectFieldsForm(@RequestParam("id") Long id, Model model) {

        try {
            model.addAttribute("projectDto", projectService.getDto(id));

            return "project-update-fields";

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            return "redirect:/projects/listAll";
        }
    }

    @PostMapping("/add")
    public String addProject(@ModelAttribute("projectCreationDto") @Valid ProjectCreationDto projectCreationDto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            projectCreationDto.setProjectManagerDtoList(projectManagerService.findProfileDtoList());

            return "project-create";

        } else {
            projectService.create(projectCreationDto);

            return "redirect:/projects/listAll";
        }
    }

    @PostMapping("/updateFields")
    public String updateProjectFields(@ModelAttribute("projectDto") @Valid ProjectDto projectDto,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "project-update-fields";

        } else {
            try {
                projectService.updateFields(projectDto);

                return "redirect:/projects/listAll";

            } catch (ProjectNotFoundException e) {
                e.printStackTrace();

                return "redirect:/projects/listAll";
            }
        }
    }

    @GetMapping("/delete")
    public String deleteProject(@RequestParam("id") Long id) {
        projectService.delete(id);

        return "redirect:/projects/listAll";
    }


    @GetMapping("/showDetailsPage")
    public String showProjectDetailsPage(@RequestParam("id") Long id, Model model) {

        try {
            model.addAttribute("project", projectService.findProjectById(id));

            return "project-details";

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            return "redirect:/projects/";
        }
    }

}
