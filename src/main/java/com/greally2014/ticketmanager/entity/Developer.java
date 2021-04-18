package com.greally2014.ticketmanager.entity;

import org.hibernate.validator.constraints.EAN;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("DEVELOPER")
public class Developer extends User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UsersProjects> usersProjects;

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL)
    private List<DevelopersTickets> developersTickets;

    public Developer(String username, String password,
                     String firstName, String lastName,
                     String gender,
                     String email, String phoneNumber) {
        super(username, password, firstName, lastName, gender, email, phoneNumber);
    }

    public Developer() {
    }

    public List<UsersProjects> getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(List<UsersProjects> usersProjects) {
        this.usersProjects = usersProjects;
    }

    public List<DevelopersTickets> getDevelopersTickets() {
        return developersTickets;
    }

    public void setDevelopersTickets(List<DevelopersTickets> developersTickets) {
        this.developersTickets = developersTickets;
    }
}
