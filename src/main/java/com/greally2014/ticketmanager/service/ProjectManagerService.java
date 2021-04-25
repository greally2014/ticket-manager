package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectManagerRepository;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectManagerService {

    private final ProjectManagerRepository projectManagerRepository;

    public ProjectManagerService(ProjectManagerRepository projectManagerRepository) {
        this.projectManagerRepository = projectManagerRepository;
    }

    @Transactional
    public List<ProjectManager> findAll(List<Long> idList) {
        return projectManagerRepository.findAllById(idList);
    }

    @Transactional
    public List<UserProfileDto> findProfileDtoList() {
        return projectManagerRepository.findAll().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Project> findProjects(String username) {
        return projectManagerRepository.findByUsername(username).getUsersProjects().stream()
                .map(UsersProjects::getProject)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Ticket> findTickets(String username) {
        return findProjects(username).stream()
                .map(Project::getTickets)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<User> findAllEmployees(String username) {
        return findProjects(username).stream()
                .map(Project::getUsersProjects)
                .flatMap(List::stream)
                .map(UsersProjects::getUser)
                .collect(Collectors.toList());
    }
}
