package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.ProjectDto;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final CustomUserDetailsService customUserDetailsService;

    private final ProjectManagerService projectManagerService;

    private final ProjectRepository projectRepository;

    public ProjectService(CustomUserDetailsService customUserDetailsService,
                          ProjectManagerService projectManagerService,
                          ProjectRepository projectRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.projectManagerService = projectManagerService;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project findProjectById(Long id) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        projectOptional.orElseThrow(() -> new ProjectNotFoundException("Project not found. Id: " + id));
        return projectOptional.get();
    }


    public List<Project> findAllByUsernameOrderByTitle(String username) {
        User user = customUserDetailsService.loadUserByUsername(username).getUser();
        List<Project> projects;

        if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_PROJECT_MANAGER"))) {
            projects = projectManagerService.findProjects(username);
        } else {
            projects = projectRepository.findAll();
        }

        projects.sort(Comparator.comparing(Project::getTitle));

        return projects;
    }

    @Transactional
    public void create(ProjectCreationDto projectCreationDto) {
        Project project = new Project(
                projectCreationDto.getProjectDto().getTitle(),
                projectCreationDto.getProjectDto().getDescription()
        );

        project.setCreator(
                (GeneralManager) customUserDetailsService.loadUserByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName()).getUser()
        );

        List<Long> selectedProjectManagerIds = projectCreationDto.getProjectManagerDtoList().stream()
                .filter(UserProfileDto::getFlag)
                .map(UserProfileDto::getId)
                .collect(Collectors.toList());

        List<UsersProjects> usersProjects = projectManagerService.findAllById(selectedProjectManagerIds).stream()
                .map(o -> (new UsersProjects(o, project)))
                .collect(Collectors.toList());

        project.setUsersProjects(usersProjects);

        projectRepository.save(project);
    }

    @Transactional
    public void updateFields(ProjectDto projectDto) throws ProjectNotFoundException {
        Project project = findProjectById(projectDto.getId());
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
    }

    @Transactional
    public void delete(Long id) {
        // check if username exists and handle exception / have denied access redirect / error handler
        // check if project already deleted
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectDto getDto(Long id) throws ProjectNotFoundException {
        return new ProjectDto(findProjectById(id));
    }
}
