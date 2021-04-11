package com.greally2014.ticketmanager.entity.user.specialization;

import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(value = "PROJECT_MANAGER")
public class ProjectManager extends User {

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinTable(name = "users_projects",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects;

    public ProjectManager(String username, String password, String firstName, String lastName,
                          String  gender, String email, String phoneNumber) {
        super(username, password, firstName, lastName, gender, email, phoneNumber);
    }

    public ProjectManager() {
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
