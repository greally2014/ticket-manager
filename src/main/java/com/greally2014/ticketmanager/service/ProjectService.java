package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.Role;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.formModel.FormProject;
import com.greally2014.ticketmanager.formModel.ProfileFormUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private  ProjectManagerService projectManagerService;

    @Autowired
    private ProjectRepository projectRepository;


    public List<Project> findAllByUserOrderByTitle(String username) {
        User user = customUserDetailsService.loadUserByUsername(username).getUser();
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        List<Project> projects;

        if (roleNames.contains("ROLE_PROJECT_MANAGER")) {
            projects = projectRepository.findAllByProjectManagersIdOrderByTitle(user.getId());
        } else {
            projects = projectRepository.findAllByOrderByTitle();
        }
        return projects;
    }

    public void deleteById(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    public void save(ProjectCreationDto projectCreationDto) {
        FormProject formProject = projectCreationDto.getFormProject();
        List<ProfileFormUser> projectManagerFormList = projectCreationDto.getFormProjectManagerList();

        Project project = new Project(
                formProject.getTitle(),
                formProject.getDescription(),
                formProject.getDateCreated()
        );

        List<Long> selectedIds =
                projectManagerFormList.stream()
                        .filter(ProfileFormUser::getFlag)
                        .map(ProfileFormUser::getId)
                        .collect(Collectors.toList());

        project.setProjectManagers(projectManagerService.findAllById(selectedIds));
        projectRepository.save(project);
    }

    public void updateFields(FormProject formProject) {
        Project project = projectRepository.getOne(formProject.getId());
        project.setTitle(formProject.getTitle());
        project.setDescription(formProject.getDescription());
        projectRepository.save(project);
    }

    public FormProject getFormProject(Long id) {
        return new FormProject(projectRepository.getOne(id));
    }
}
