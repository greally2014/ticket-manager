package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.user.specialization.ProjectManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectManagerRepository extends JpaRepository<ProjectManager, Long> {
    ProjectManager findByUsername(String username);
}
