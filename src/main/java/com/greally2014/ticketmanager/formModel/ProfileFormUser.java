package com.greally2014.ticketmanager.formModel;

import com.greally2014.ticketmanager.validation.UniqueEmail;
import com.greally2014.ticketmanager.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileFormUser {

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

    public ProfileFormUser(@NotNull(message = "Username is required")
                           @Size(min = 1, max = 50, message = "Invalid format")
                                   String username,
                           @NotNull(message = "First name is required")
                           @Size(min = 1, max = 50, message = "Invalid format")
                                   String firstName,
                           @NotNull(message = "Last name is required")
                           @Size(min = 1, max = 50, message = "Invalid format")
                                   String lastName,
                           @Size(min = 1, max = 50, message = "Email is required")
                                   String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public ProfileFormUser() {
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

    @Override
    public String toString() {
        return "ProfileUser{" +
                "userName='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
