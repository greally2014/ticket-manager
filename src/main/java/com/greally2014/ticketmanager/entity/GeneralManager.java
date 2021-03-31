package com.greally2014.ticketmanager.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "GENERAL_MANAGER")
public class GeneralManager extends User {

    public GeneralManager(String userName, String password, String firstName, String lastName, String email) {
        super(userName, password, firstName, lastName, email);
    }

    public GeneralManager() {
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "GeneralManager{}";
    }
}
