package com.greally2014.ticketmanager.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "GENERAL_MANAGER")
public class GeneralManager extends User {

    public GeneralManager(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
    }

    public GeneralManager() {
    }

}
