package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByProjectManagersId(Long id);
    List<Project> findAllByTitleContainsAllIgnoreCase(String title);
}
