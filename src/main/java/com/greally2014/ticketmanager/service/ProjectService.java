package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.Role;
import com.greally2014.ticketmanager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAllByUserAndUserRole(String username) {

        User user = customUserDetailsService.loadUserByUsername(username).getUser();
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        List<Project> projects = new ArrayList<>();

        if (roleNames.contains("ROLE_PROJECT_MANAGER")) {
            projects = projectRepository.findAllByProjectManagersId(user.getId());
        } else {
            projects = projectRepository.findAll();
        }
        return projects;
    }

    public void deleteById(Long projectId) {
        projectRepository.deleteById(projectId);
    }
}
