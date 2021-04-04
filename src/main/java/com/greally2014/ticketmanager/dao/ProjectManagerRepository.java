package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.ProjectManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectManagerRepository extends JpaRepository<ProjectManager, Long> {
    List<ProjectManager> findAllByOrderByUsername();
}
