package com.greally2014.ticketmanager.formModel;

import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.validation.UniqueEmail;
import com.greally2014.ticketmanager.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileFormUser {

    private Long id;

    @NotNull(message = "Username is required")
    @Size(min = 1, max = 50, message = "Invalid format")
    private String username;

    @NotNull(message = "First name is required")
    @Size(min = 1, max = 50, message = "Invalid format")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Invalid format")
    private String lastName;

    @ValidEmail
    @UniqueEmail
    @Size(min = 1, max = 50, message = "Email is required")
    private String email;

    private boolean flag;

    public ProfileFormUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public ProfileFormUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
