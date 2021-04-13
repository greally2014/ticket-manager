package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.ProjectDto;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.Role;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.entity.UsersProjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final CustomUserDetailsService customUserDetailsService;

    private  final ProjectManagerService projectManagerService;

    private final ProjectRepository projectRepository;

    public ProjectService(CustomUserDetailsService customUserDetailsService,
                          ProjectManagerService projectManagerService,
                          ProjectRepository projectRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.projectManagerService = projectManagerService;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public List<Project> findAllByUsername(String username) {
        User user = customUserDetailsService.loadUserByUsername(username).getUser();
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        List<Project> projects;

        if (roleNames.contains("ROLE_PROJECT_MANAGER")) {
            projects = projectManagerService.getProjects(username);
        } else {
            projects = projectRepository.findAllByOrderByTitle();
        }

        return projects;
    }

    @Transactional
    public void create(ProjectCreationDto projectCreationDto) {
        ProjectDto projectDto = projectCreationDto.getProjectDto();
        Project project = new Project(projectDto.getTitle(), projectDto.getDescription());

        List<UserProfileDto> projectManagerDtoList = projectCreationDto.getProjectManagerDtoList();

        List<Long> selectedIds =
                projectManagerDtoList.stream()
                        .filter(UserProfileDto::getFlag)
                        .map(UserProfileDto::getId)
                        .collect(Collectors.toList());

        List<UsersProjects> usersProjects = projectManagerService.findAllById(selectedIds).stream()
                .map(o -> (new UsersProjects(o, project)))
                .collect(Collectors.toList());

        project.setUsersProjects(usersProjects);
        projectRepository.save(project);
    }

    @Transactional
    public void delete(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public void updateFields(ProjectDto projectDto) {
        Project project = projectRepository.getOne(projectDto.getId());
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
    }

    @Transactional
    public ProjectDto getDto(Long id) {
        return new ProjectDto(projectRepository.getOne(id));
    }
}
