package com.greally2014.ticketmanager.formModel;

import com.greally2014.ticketmanager.validation.UniqueEmail;
import com.greally2014.ticketmanager.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileUser {

    @NotNull(message = "Username is required")
    @Size(min = 1, max = 50, message = "Username is required")
    private String username;

    @NotNull(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name is required")
    private String lastName;

    @ValidEmail
    @UniqueEmail
    @Size(min = 1, max = 50, message = "Email is required")
    private String email;

    public ProfileUser(@NotNull(message = "Username is required")
                       @Size(min = 1, max = 50, message = "Username is required")
                               String username,
                       @NotNull(message = "First name is required")
                       @Size(min = 1, max = 50, message = "First name is required")
                               String firstName, @NotNull(message = "Last name is required")
                       @Size(min = 1, max = 50, message = "Last name is required")
                               String lastName,
                       @Size(min = 1, max = 50, message = "Email is required")
                               String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public ProfileUser() {
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
