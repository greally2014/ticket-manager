package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectManagerRepository;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.user.specialization.ProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectManagerService {

    @Autowired
    private ProjectManagerRepository projectManagerRepository;

    @Transactional
    public List<ProjectManager> findAllById(List<Long> idList) {
        return projectManagerRepository.findAllById(idList);
    }

    @Transactional
    public List<UserProfileDto> getProfileDtoList() {
        return projectManagerRepository.findAll().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Project> getProjects(String username) {
        return projectManagerRepository.findByUsername(username).getProjects();
    }

}
