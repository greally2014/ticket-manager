package com.greally2014.ticketmanager.formModel;

import com.greally2014.ticketmanager.validation.FieldMatch;
import com.greally2014.ticketmanager.validation.UniqueEmail;
import com.greally2014.ticketmanager.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch.List(
        @FieldMatch(first = "password", second = "matchingPassword")
)
public class RegistrationUser {

    @NotNull(message = "Username is required")
    @Size(min = 1, max = 50, message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 1, message = "Password is required")
    private String password;

    @NotNull(message = "Confirmation password is required")
    @Size(min = 1, message = "Confirmation password is required")
    private String matchingPassword;

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

    @NotNull(message = "Please select a role")
    @Size(min = 1, message = "Please select a role")
    private String formRole;

    public RegistrationUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
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

    public String getFormRole() {
        return formRole;
    }

    public void setFormRole(String formRole) {
        this.formRole = formRole;
    }

    @Override
    public String toString() {
        return "RegistrationUser{" +
                "userName='" + username + '\'' +
                ", password='" + password + '\'' +
                ", matchingPassword='" + matchingPassword + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
