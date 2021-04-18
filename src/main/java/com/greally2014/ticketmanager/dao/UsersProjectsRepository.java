package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.UsersProjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersProjectsRepository extends JpaRepository<UsersProjects, Long> {

    void deleteByUserIdAndProjectId(Long userId, Long projectId);

    UsersProjects findByUserIdAndProjectId(Long userId, Long projectId);
}
