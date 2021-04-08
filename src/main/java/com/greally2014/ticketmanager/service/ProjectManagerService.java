package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectManagerRepository;
import com.greally2014.ticketmanager.entity.ProjectManager;
import com.greally2014.ticketmanager.formModel.ProfileFormUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectManagerService {

    @Autowired
    private ProjectManagerRepository projectManagerRepository;

    public List<ProjectManager> findAllById(List<Long> idList) {
        return projectManagerRepository.findAllById(idList);
    }

    public List<ProfileFormUser> getFormList() {
        return projectManagerRepository.findAll().stream()
                .map(ProfileFormUser::new)
                .collect(Collectors.toList());
    }

}
