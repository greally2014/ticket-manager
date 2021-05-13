package com.greally2014.ticketmanager.entity;


import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("DEVELOPER")
public class Developer extends User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsersProjects> usersProjects;

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DevelopersTickets> developersTickets;

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
