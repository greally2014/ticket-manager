package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.*;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/projects")
@PreAuthorize("hasAnyRole('GENERAL_MANAGER', 'PROJECT_MANAGER')")
public class ProjectController {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private final ProjectService projectService;

    private final ProjectManagerService projectManagerService;

    private final CustomUserDetailsService customUserDetailsService;

    public ProjectController(ProjectService projectService,
                             ProjectManagerService projectManagerService,
                             CustomUserDetailsService customUserDetailsService) {
        this.projectService = projectService;
        this.projectManagerService = projectManagerService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/listAll")
    public String listAllProjects(Model model) {
        // check if username exists and handle exception / have denied access redirect / error handler
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("projects",
                projectService.findAllByUsernameOrderByTitle(principal.getName())
        );

        return "project-list";

    }

    @GetMapping("/showAddForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showAddProjectForm(Model model) {
        // check if username exists and handle exception / have denied access redirect / error handler
        model.addAttribute("projectCreationDto",
                new ProjectCreationDto(
                        new ProjectDto(),
                        projectManagerService.findProfileDtoList()
                )
        );

        return "project-add";
    }

    @GetMapping("/showUpdateFieldsForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
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
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProject(@ModelAttribute("projectCreationDto") @Valid ProjectCreationDto projectCreationDto,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            projectCreationDto.setProjectManagerDtoList(projectManagerService.findProfileDtoList());

            return "project-add";

        } else {
            // check if username exists and handle exception / have denied access redirect / error handler
            projectService.add(projectCreationDto);

            return "redirect:/projects/listAll";
        }
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProjectUser(@ModelAttribute("projectAddUserDto") @Valid ProjectAddUserDto projectAddUserDto,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            return "project-add-user";

        } else {
            // check if username exists and handle exception / have denied access redirect / error handler
            try {
                projectService.addUsers(projectAddUserDto);

                return showProjectDetailsPage(projectAddUserDto.getProjectDto().getId(), model);

            } catch (ProjectNotFoundException e) {
                e.printStackTrace();

                return "redirect:/projects/listAll";

            } catch (UserNotFoundException e) {
                e.printStackTrace();

                return showProjectDetailsPage(projectAddUserDto.getProjectDto().getId(), model);
            }
        }
    }

    @PostMapping("/updateFields")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String updateProjectFields(@ModelAttribute("projectDto") @Valid ProjectDto projectDto,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            return "project-update-fields";

        } else {
            try {
                // check if username exists and handle exception / have denied access redirect / error handler
                // check if project deleted
                projectService.updateFields(projectDto);

                return showProjectDetailsPage(projectDto.getId(), model);

            } catch (ProjectNotFoundException e) {

                return "redirect:/projects/listAll";
            }
        }
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String deleteProject(@RequestParam("id") Long id) {
        // check if username exists and handle exception / have denied access redirect / error handler
        // check if project already deleted
        try {
            projectService.delete(id);

            return "redirect:/projects/listAll";

        } catch (ProjectNotFoundException  e) {

            return "redirect:/projects/listAll";
        }
    }

    @GetMapping("/showDetailsPage")
    public String showProjectDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            // check if username exists and handle exception / have denied access redirect / error handler
            // check if project deleted
            model.addAttribute("projectDetailsDto", projectService.getDetailsDto(id));

            return "project-details";

        } catch (ProjectNotFoundException e) {

            return "redirect:/projects/listAll";
        }
    }

    @GetMapping("/showAddUserForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProjectUser(@RequestParam("id") Long id,
                                 @RequestParam("roleIdentifier") Long roleIdentifier,
                                 Model model) {
        try {
            ProjectDetailsDto projectDetailsDto = projectService.getDetailsDto(id);
            List<UserProfileDto> userDtoList =
                    projectService.findAllUserProfileDtoNotAdded(projectDetailsDto, roleIdentifier);
            model.addAttribute("projectAddUserDto",
                    new ProjectAddUserDto(projectDetailsDto.getProjectDto(), userDtoList));

            return "project-add-user";

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            return showProjectDetailsPage(id, model);
        }
    }

    @GetMapping("/kickUser")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String kickProjectUser(@RequestParam("userId") Long userId,
                                  @RequestParam("projectId") Long projectId,
                                  Model model) {
        try {
            projectService.kickUser(userId, projectId);

        } catch (Exception e) {
            // nothing??
        }

        return showProjectDetailsPage(projectId, model);
    }

    @GetMapping("showEmployeeDetails")
    public String showEmployeeDetailsPage(@RequestParam("userId") Long userId,
                                          @RequestParam("projectId") Long projectId,
                                          Model model) {
        try {
            model.addAttribute("employee", customUserDetailsService.findById(userId));
            model.addAttribute("projectId", projectId);

            return "project-employee-details";

        } catch (UserNotFoundException e) {
            e.printStackTrace();

            return showProjectDetailsPage(projectId, model);
        }
    }
}
