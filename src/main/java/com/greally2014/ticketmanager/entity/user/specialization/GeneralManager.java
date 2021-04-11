package com.greally2014.ticketmanager.entity.user.specialization;

import com.greally2014.ticketmanager.entity.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "GENERAL_MANAGER")
public class GeneralManager extends User {

    public GeneralManager(String username, String password, String firstName, String lastName,
                          String gender, String email, String phoneNumber) {
        super(username, password, firstName, lastName, gender, email, phoneNumber);
    }

    public GeneralManager() {
    }

}
