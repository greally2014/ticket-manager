package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.UsersProjects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersProjectsRepository extends JpaRepository<UsersProjects, Long> {

    void deleteAllByUserIdAndProjectId(Long userId, Long projectId);

    UsersProjects findByUserIdAndProjectId(Long userId, Long projectId);
}
