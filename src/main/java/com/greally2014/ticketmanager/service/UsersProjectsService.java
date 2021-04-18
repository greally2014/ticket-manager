package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.UsersProjectsRepository;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.entity.UsersProjects;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersProjectsService {

    private final UsersProjectsRepository usersProjectsRepository;

    public UsersProjectsService(UsersProjectsRepository usersProjectsRepository) {
        this.usersProjectsRepository = usersProjectsRepository;
    }

    public UsersProjects findByUserIdAndProjectId(Long userId, Long projectId) {
        return usersProjectsRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public void deleteByUserIdAndProjectId(Long userId, Long projectId) {
        usersProjectsRepository.deleteByUserIdAndProjectId(userId, projectId);
    }

    public List<UsersProjects> findAllByProjectId(Long id) {
        return usersProjectsRepository.findAllByProjectId(id);
    }

    public void add(User user, Project project) {
        UsersProjects usersProjects = new UsersProjects(user, project);
        usersProjectsRepository.save(usersProjects);
    }
}
