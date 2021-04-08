package com.greally2014.ticketmanager.formModel;

import com.greally2014.ticketmanager.validation.FieldMatch;
import com.greally2014.ticketmanager.validation.UniqueEmail;
import com.greally2014.ticketmanager.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch.List(
        @FieldMatch(first = "password", second = "matchingPassword")
)
public class RegistrationFormUser {

    @NotNull(message = "Username is required")
    @Size(min = 1, max = 50, message = "Invalid format")
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 1, message = "Invalid format")
    private String password;

    @NotNull(message = "Confirmation password is required")
    @Size(min = 1, message = "Invalid format")
    private String matchingPassword;

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

    @NotNull(message = "Role must be selected")
    @Size(min = 1, message = "Role must be selected")
    private String formRole;

    public RegistrationFormUser() {
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
}
