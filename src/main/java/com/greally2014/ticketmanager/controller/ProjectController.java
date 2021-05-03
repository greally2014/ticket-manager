package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.project.ProjectAddUserDto;
import com.greally2014.ticketmanager.dto.project.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.project.ProjectDto;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.*;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public String listAllProjects(Model model, Principal principal) {
        model.addAttribute("projects",
                projectService.findAllByRole(principal.getName())
        );

        return "project/project-list";

    }

    @GetMapping("/showAddForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showAddProjectForm(Model model) {
        model.addAttribute("projectCreationDto", projectService.getCreationDto());

        return "project/project-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProject(@ModelAttribute("projectCreationDto") @Valid ProjectCreationDto projectCreationDto,
                             BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            projectCreationDto.setProjectManagerDtoList(projectManagerService.findProfileDtoList());

            return "project/project-add";

        } else {
            try {
                projectService.add(projectCreationDto, principal.getName());

                return "redirect:/projects/listAll";

            } catch (UserNotFoundException e) {

                return "redirect:/projects/showAddForm";
            }
        }
    }

    @GetMapping("/showDetailsPage")
    public String showProjectDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("projectDetailsDto", projectService.getDetailsDto(id));

            return "project/project-details";

        } catch (ProjectNotFoundException e) {

            return "redirect:/projects/listAll";
        }
    }

    @GetMapping("/showUpdateFieldsForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showUpdateProjectFieldsForm(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("projectDto", projectService.getDto(id));

            return "project/project-update-fields";

        } catch (ProjectNotFoundException e) {

            return "redirect:/projects/listAll";
        }
    }

    @PostMapping("/updateFields")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String updateProjectFields(@ModelAttribute("projectDto") @Valid ProjectDto projectDto,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            return "project/project-update-fields";

        } else {
            try {
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
        try {
            projectService.delete(id);

        } catch (ProjectNotFoundException  e) {
            //nothing
        }

        return "redirect:/projects/listAll";
    }

    @GetMapping("/showAddUserForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showAddProjectUserForm(@RequestParam("id") Long id,
                                         @RequestParam("roleIdentifier") Long roleIdentifier,
                                         Model model) {
        try {
            ProjectDto projectDto = projectService.getDto(id);
            List<UserProfileDto> userDtoList =
                    projectService.findAllUserDtoNotAdded(id, roleIdentifier);
            ProjectAddUserDto projectAddUserDto = new ProjectAddUserDto(projectDto, userDtoList);
            projectAddUserDto.setRoleIdentifier(roleIdentifier);

            model.addAttribute("projectAddUserDto", projectAddUserDto);

            return "project/project-add-user";

        } catch (ProjectNotFoundException e) {

            return "redirect:/projects/listAll";
        }
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProjectUser(@ModelAttribute("projectAddUserDto") @Valid ProjectAddUserDto projectAddUserDto,
                                 BindingResult bindingResult, Model model) {
        Long projectId = projectAddUserDto.getProjectDto().getId();
        Long roleIdentifier = projectAddUserDto.getRoleIdentifier();

        if (bindingResult.hasErrors()) {
            try {
                projectAddUserDto.setUserDtoList(projectService.findAllUserDtoNotAdded(projectId, roleIdentifier));

                return "project/project-add-user";

            } catch (ProjectNotFoundException e) {

                return "redirect:/projects/listAll";
            }

        } else {
            try {
                projectService.addUsers(projectAddUserDto);

                return showProjectDetailsPage(projectId, model);

            } catch (UserNotFoundException e) {

                return showAddProjectUserForm(projectId, roleIdentifier, model);

            } catch (ProjectNotFoundException e) {

                return "redirect:/projects/listAll";
            }
        }
    }

    @GetMapping("/kickUser")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String kickProjectUser(@RequestParam("userId") Long userId,
                                  @RequestParam("projectId") Long projectId,
                                  Model model) {
        try {
            projectService.kickUser(userId, projectId);

        } catch (UserNotFoundException e) {
            // nothing

        } catch (ProjectNotFoundException e) {

            return "redirect:/projects/listAll";
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

            return "project/project-employee-details";

        } catch (UserNotFoundException e) {

            return showProjectDetailsPage(projectId, model);
        }
    }

    @GetMapping("/deleteEmployee")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String deleteProjectEmployee(@RequestParam("employeeId") Long employeeId,
                                        @RequestParam("projectId") Long projectId,
                                        Model model) {

        try {
            customUserDetailsService.delete(employeeId);

        } catch (UserNotFoundException e) {
            // message
        }

        return showProjectDetailsPage(projectId, model);
    }
}
