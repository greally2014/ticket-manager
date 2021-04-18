package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users_projects")
public class UsersProjects {

    @EmbeddedId
    private UsersProjectsKey id;

    @ManyToOne
    @MapsId(value = "userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId(value = "projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "date_assigned")
    private LocalDate dateAssigned;

    public UsersProjects(User user, Project project, LocalDate dateAssigned) {
        this.id = new UsersProjectsKey();
        this.user = user;
        this.project = project;
        this.dateAssigned = dateAssigned;
    }

    public UsersProjects(User user, Project project) {
        this.id = new UsersProjectsKey();
        this.user = user;
        this.project = project;
        this.dateAssigned = LocalDate.now();
    }

    public UsersProjects() {
    }

    public UsersProjectsKey getId() {
        return id;
    }

    public void setId(UsersProjectsKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDate getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDate dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
}
