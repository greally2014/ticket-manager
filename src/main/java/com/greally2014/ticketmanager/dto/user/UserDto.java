package com.greally2014.ticketmanager.dto.user;

import com.greally2014.ticketmanager.validation.email.uniqueEmail.UniqueEmail;
import com.greally2014.ticketmanager.validation.email.validEmail.ValidEmail;
import com.greally2014.ticketmanager.validation.phoneNumber.ValidPhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto {

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

    public UserDto(String firstName, String lastName, String gender, String email, AddressDto address, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public UserDto() {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
}
