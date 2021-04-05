package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrderByTitle();
    List<Project> findAllByProjectManagersIdOrderByTitle(Long id);

    @Query("select p from Project p where concat(p.title, p.dateCreated) like %?1% order by p.title")
    List<Project> searchByParameterOrderByTitle(String searchParameter);
}
