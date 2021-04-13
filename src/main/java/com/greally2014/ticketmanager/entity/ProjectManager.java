package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(value = "PROJECT_MANAGER")
public class ProjectManager extends User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UsersProjects> usersProjects;

    public ProjectManager(String username, String password,
                          String firstName, String lastName,
                          String gender,
                          String email, String phoneNumber) {
        super(username, password, firstName, lastName, gender, email, phoneNumber);
    }

    public ProjectManager() {
    }

    public List<UsersProjects> getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(List<UsersProjects> usersProjects) {
        this.usersProjects = usersProjects;
    }

}
