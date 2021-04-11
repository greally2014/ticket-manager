package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.ProjectDto;
import com.greally2014.ticketmanager.service.ProjectManagerService;
import com.greally2014.ticketmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectManagerService projectManagerService;

    @GetMapping("/list")
    public String list(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("projects",
                projectService.findAllByUsername(principal.getName()));

        return "project-list";
    }

    @GetMapping("/showCreateForm")
    public String showCreateForm(Model model) {
        model.addAttribute("projectCreationDto",
                new ProjectCreationDto(
                        new ProjectDto(),
                        projectManagerService.getProfileDtoList()
                )
        );

        return "project-create";
    }

    @GetMapping("/showUpdateFieldsForm")
    public String showUpdateFieldsForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("projectDto", projectService.getDto(id));

        return "project-update-fields";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute("projectCreationDto") @Valid ProjectCreationDto projectCreationDto,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            projectCreationDto.setProjectManagerDtoList(projectManagerService.getProfileDtoList());

            return "project-create";

        } else {
            projectService.create(projectCreationDto);

            return "redirect:/projects/list";
        }
    }

    @PostMapping("/updateFields")
    public String updateFields(@ModelAttribute("projectDto") @Valid ProjectDto projectDto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "project-update-fields";

        } else {
            projectService.updateFields(projectDto);

            return "redirect:/projects/list";
        }
    }

    @GetMapping("/delete")
    public String delete(@ModelAttribute("id") Long id) {
        projectService.delete(id);

        return "redirect:/projects/list";
    }
}
