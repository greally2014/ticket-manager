package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(value = "PROJECT_MANAGER")
public class ProjectManager extends User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsersProjects> usersProjects;

    public ProjectManager() {
    }

    public List<UsersProjects> getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(List<UsersProjects> usersProjects) {
        this.usersProjects = usersProjects;
    }

}
