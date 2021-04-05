package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.Role;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ProjectRepository projectRepository;

    public Project findById(Long projectId) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        projectOptional.orElseThrow(() -> new ProjectNotFoundException("Not found (id): " + projectId));
        return projectOptional.get();
    }

    public List<Project> findAllByUserAndUserRoleOrderByTitle(String username) {

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

    public void save(Project project) {
        projectRepository.save(project);
    }

    public void deleteById(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    public List<Project> searchByParameterOrderByTitle(String searchParameter) {
        return projectRepository.searchByParameterOrderByTitle(searchParameter);
    }
}
