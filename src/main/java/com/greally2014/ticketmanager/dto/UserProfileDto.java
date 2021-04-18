package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.DevelopersTickets;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.entity.UsersProjects;
import com.greally2014.ticketmanager.validation.email.uniqueEmail.UniqueEmail;
import com.greally2014.ticketmanager.validation.email.validEmail.ValidEmail;
import com.greally2014.ticketmanager.validation.phoneNumber.ValidPhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class UserProfileDto {

    private Long id;

    private String username;

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

    private UsersProjects usersProjects;

    private DevelopersTickets developersTickets;

    private boolean flag;

    public UserProfileDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.address = new AddressDto(user.getAddress());
        this.phoneNumber = user.getPhoneNumber();
    }

    public UserProfileDto() {
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

    public UsersProjects getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(UsersProjects usersProjects) {
        this.usersProjects = usersProjects;
    }

    public DevelopersTickets getDevelopersTickets() {
        return developersTickets;
    }

    public void setDevelopersTickets(DevelopersTickets developersTickets) {
        this.developersTickets = developersTickets;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
