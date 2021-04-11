package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.validation.fieldMatch.FieldMatch;
import com.greally2014.ticketmanager.validation.email.uniqueEmail.UniqueEmail;
import com.greally2014.ticketmanager.validation.email.validEmail.ValidEmail;
import com.greally2014.ticketmanager.validation.password.ValidPassword;
import com.greally2014.ticketmanager.validation.phoneNumber.ValidPhoneNumber;
import com.greally2014.ticketmanager.validation.username.UniqueUsername;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch.List(
        @FieldMatch(first = "password", second = "matchingPassword")
)
public class RegistrationDto {

    @NotNull(message = "Username is required")
    @Size(min = 1, max = 50, message = "Username is required")
    @UniqueUsername
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must contain at least 8 characters")
    @ValidPassword
    private String password;
    private String matchingPassword;

    @NotNull(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name is required")
    private String lastName;

    @NotNull(message = "Please select a gender" )
    @Size(min = 1, max = 50, message = "Please select a gender")
    private String gender;

    @NotNull(message = "Email is required")
    @Size(min = 1, max = 50, message = "Email is required")
    @ValidEmail
    @UniqueEmail
    private String email;

    @Valid
    private AddressDto address;

    @NotNull(message = "Phone number is required")
    @Size(min = 1, max = 20, message = "Phone number is required")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotNull(message = "Please select a role")
    @Size(min = 1, max = 50, message = "Please select a role")
    private String formRole;

    public RegistrationDto() {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFormRole() {
        return formRole;
    }

    public void setFormRole(String formRole) {
        this.formRole = formRole;
    }
}
