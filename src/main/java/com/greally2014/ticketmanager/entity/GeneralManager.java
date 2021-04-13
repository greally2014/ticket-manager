package com.greally2014.ticketmanager.entity;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@DiscriminatorValue(value = "GENERAL_MANAGER")
public class GeneralManager extends User {

    public GeneralManager(String username, String password,
                          String firstName, String lastName,
                          String gender,
                          String email, String phoneNumber) {
        super(username, password, firstName, lastName, gender, email, phoneNumber);
    }

    public GeneralManager() {
    }

    @OneToMany(mappedBy = "generalManager",
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH}
    )
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
