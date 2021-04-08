package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.formModel.FormProject;
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
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectManagerService projectManagerService;

    @GetMapping("/list")
    public String listProjects(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        List<Project> projects = projectService.findAllByUserOrderByTitle(principal.getName());

        model.addAttribute("projects", projects);
        return "project-list";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        ProjectCreationDto projectCreationDto = new ProjectCreationDto(
                new FormProject(),
                projectManagerService.getFormList());

        model.addAttribute("projectCreationDto", projectCreationDto);
        return "project-form";
    }

    @GetMapping("/showFormForUpdateFields")
    public String showFormForUpdate(@RequestParam("id") Long id, Model model) {
        model.addAttribute("formProject", projectService.getFormProject(id));
        return "project-form-update-fields";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute("projectCreationDto") @Valid ProjectCreationDto projectCreationDto,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            projectCreationDto.setFormProjectManagerList(projectManagerService.getFormList());
            return "project-form";

        } else {
            projectService.save(projectCreationDto);
            return "redirect:/projects/list";
        }
    }

    @PostMapping("/updateFields")
    public String updateFields(@ModelAttribute("formProject") @Valid FormProject formProject,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "project-form-update-fields";
        } else {
            projectService.updateFields(formProject);
            return "redirect:/projects/list";
        }
    }

    @GetMapping("/delete")
    public String deleteProject(@ModelAttribute("projectId") Long projectId) {
        projectService.deleteById(projectId);
        return "redirect:/projects/list";
    }
}
