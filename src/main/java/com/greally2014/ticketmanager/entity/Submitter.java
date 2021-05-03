package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(value = "SUBMITTER")
public class Submitter extends User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsersProjects> usersProjects;

    @OneToMany(
            mappedBy = "submitter",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH}
    )
    private List<Ticket> tickets;

    public Submitter(String username, String password,
                     String firstName, String lastName,
                     String  gender,
                     String email, String phoneNumber) {
        super(username, password, firstName, lastName, gender, email, phoneNumber);
    }

    public Submitter() {
    }

    public List<UsersProjects> getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(List<UsersProjects> usersProjects) {
        this.usersProjects = usersProjects;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

}
