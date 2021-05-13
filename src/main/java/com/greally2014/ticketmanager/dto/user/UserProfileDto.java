package com.greally2014.ticketmanager.dto.user;

import com.greally2014.ticketmanager.entity.DevelopersTickets;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.entity.UsersProjects;

public class UserProfileDto extends UserDto {

    private Long id;

    private String username;

    private String photoName;

    private UsersProjects usersProjects;

    private DevelopersTickets developersTickets;

    private boolean flag; // used for selection by checkbox, false by default

    public UserProfileDto(User user) {
        super(user.getFirstName(), user.getLastName(), user.getGender(), user.getEmail(),
                new AddressDto(user.getAddress()), user.getPhoneNumber());
        this.id = user.getId();
        this.username = user.getUsername();
        this.photoName = user.getPhoto();
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

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoImagePath() {
        if (photoName == null || id == null) return null;
        return "/src/main/resources/profile-pictures/" + id + "/" + photoName; // path to profile picture
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
