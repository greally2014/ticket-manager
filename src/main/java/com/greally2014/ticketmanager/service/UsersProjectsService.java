package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.UsersProjectsRepository;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.entity.UsersProjects;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
public class UsersProjectsService {

    private final UsersProjectsRepository usersProjectsRepository;

    public UsersProjectsService(UsersProjectsRepository usersProjectsRepository) {
        this.usersProjectsRepository = usersProjectsRepository;
    }

    @Transactional
    public UsersProjects findOne(Long userId, Long projectId) {
        return usersProjectsRepository.findByUserIdAndProjectId(userId, projectId);
    }

    @Transactional
    public void delete(Long userId, Long projectId) {
        usersProjectsRepository.deleteByUserIdAndProjectId(userId, projectId);
    }

    @Transactional
    public void add(User user, Project project) {
        usersProjectsRepository.save(new UsersProjects(user, project, LocalDate.now()));
    }
}
