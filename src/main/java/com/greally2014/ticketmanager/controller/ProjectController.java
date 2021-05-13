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

    /**
     * strips whitespace from form entries, converting them to null entries if pure whitespace.
     * pure whitespace entries are then flagged by the @NotNull annotation
     */
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

        return "project/project-list"; // table of project entries

    }

    @GetMapping("/showAddForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showAddProjectForm(Model model) {
        // dto with empty project fields and a list of existing users that can be assigned on creation
        model.addAttribute("projectCreationDto", projectService.getCreationDto());
        return "project/project-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProject(@ModelAttribute("projectCreationDto") @Valid ProjectCreationDto projectCreationDto,
                             BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            // resets list of users in case new users were added/existing users were deleted
            projectCreationDto.setProjectManagerDtoList(projectManagerService.findProfileDtoList());
            model.addAttribute("projectCreationDto", projectCreationDto);
            return "project/project-add";

        } else {
            try {
                projectService.add(projectCreationDto, principal.getName());
                return "redirect:/projects/listAll";

            } catch (UserNotFoundException e) {
                return "redirect:/projects/showAddForm"; // in the event a selected user does not exist, return the updated creation form
            }
        }
    }

    @GetMapping("/showDetailsPage")
    public String showProjectDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("projectDetailsDto", projectService.getDetailsDto(id));
            return "project/project-details";

        } catch (ProjectNotFoundException e) {
            return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
        }
    }

    @GetMapping("/showUpdateFieldsForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showUpdateProjectFieldsForm(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("projectDto", projectService.getDto(id));
            return "project/project-update-fields";

        } catch (ProjectNotFoundException e) {
            return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
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
                return showProjectDetailsPage(projectDto.getId(), model); // return details page if project is found and fields updated

            } catch (ProjectNotFoundException e) {
                return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
            }
        }
    }


    @GetMapping("/delete")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String deleteProject(@RequestParam("id") Long id) {
        try {
            projectService.delete(id);

        } catch (ProjectNotFoundException  e) {
            // assume project has been deleted
        }
        return "redirect:/projects/listAll";
    }

    @GetMapping("/showAddUserForm")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String showAddProjectUserForm(@RequestParam("id") Long id, // project id
                                         // role of user to be added (so we know what discriminator column to use to fetch users)
                                         @RequestParam("roleIdentifier") Long roleIdentifier,
                                         Model model) {
        try {
            ProjectDto projectDto = projectService.getDto(id);

            // returns all users of selected role that are not already assigned to the project
            List<UserProfileDto> userDtoList = projectService.findAllUserDtoNotAdded(id, roleIdentifier);

            // contains the data of the project, and a list of all users not currently assigned that can be assigned
            ProjectAddUserDto projectAddUserDto = new ProjectAddUserDto(projectDto, userDtoList);
            projectAddUserDto.setRoleIdentifier(roleIdentifier);

            model.addAttribute("projectAddUserDto", projectAddUserDto);
            return "project/project-add-user";

        } catch (ProjectNotFoundException e) {
            return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
        }
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('GENERAL_MANAGER')")
    public String addProjectUser(@ModelAttribute("projectAddUserDto") @Valid ProjectAddUserDto projectAddUserDto,
                                 BindingResult bindingResult, Model model) {
        Long projectId = projectAddUserDto.getProjectDto().getId();

        // role of user to be assigned (so we know what discriminator column to assign to the employee)
        Long roleIdentifier = projectAddUserDto.getRoleIdentifier();

        if (bindingResult.hasErrors()) {
            try {
                // re-fetches users not already assigned in case new users were created/existing users were deleted
                projectAddUserDto.setUserDtoList(projectService.findAllUserDtoNotAdded(projectId, roleIdentifier));
                return "project/project-add-user";

            } catch (ProjectNotFoundException e) {
                return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
            }

        } else {
            try {
                projectService.addUsers(projectAddUserDto);
                return showProjectDetailsPage(projectId, model); // if successfully assigned, return details page

            } catch (UserNotFoundException e) {
                // if any selected user is not found, return the form with an updated user list
                return showAddProjectUserForm(projectId, roleIdentifier, model);

            } catch (ProjectNotFoundException e) {
                return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
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
            // assume the user has already been kicked

        } catch (ProjectNotFoundException e) {
            return "redirect:/projects/listAll"; // return the project table if the selected project does not exist
        }

        return showProjectDetailsPage(projectId, model); // return the details page if the user is not found/is kicked
    }

    @GetMapping("showEmployeeDetails")
    public String showEmployeeDetailsPage(@RequestParam("userId") Long userId,
                                          @RequestParam("projectId") Long projectId, // used for a link back to the details page
                                          Model model) {
        try {
            model.addAttribute("employee", customUserDetailsService.findById(userId));
            model.addAttribute("projectId", projectId);
            return "project/project-employee-details";

        } catch (UserNotFoundException e) {
            return showProjectDetailsPage(projectId, model); // return the details page if the user is not found
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
            // assume already deleted
        }
        return showProjectDetailsPage(projectId, model); // return the details page if the user is not found/is deleted
    }
}
