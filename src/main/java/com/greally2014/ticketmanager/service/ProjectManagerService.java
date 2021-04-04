package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectManagerRepository;
import com.greally2014.ticketmanager.entity.ProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectManagerService {
    
    @Autowired
    private ProjectManagerRepository projectManagerRepository;

    public List<ProjectManager> findAllOrderByUsername() {
        return projectManagerRepository.findAllByOrderByUsername();
    }
}
