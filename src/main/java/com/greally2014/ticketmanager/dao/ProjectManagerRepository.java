package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.ProjectManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectManagerRepository extends JpaRepository<ProjectManager, Long> {
}
