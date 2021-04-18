package com.greally2014.ticketmanager.dto;


import com.greally2014.ticketmanager.validation.selected.Selected;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ProjectAddUserDto {

    private ProjectDto projectDto;

    private Long roleIdentifier;

    @NotNull(message = "There are no users to add")
    @Selected(message = "Please select a user")
    private List<UserProfileDto> userDtoList;

    public ProjectAddUserDto(ProjectDto projectDto, List<UserProfileDto> userDtoList) {
        this.projectDto = projectDto;
        this.userDtoList = userDtoList;
    }

    public ProjectDto getProjectDto() {
        return projectDto;
    }

    public void setProjectDto(ProjectDto projectDto) {
        this.projectDto = projectDto;
    }

    public Long getRoleIdentifier() {
        return roleIdentifier;
    }

    public void setRoleIdentifier(Long roleIdentifier) {
        this.roleIdentifier = roleIdentifier;
    }

    public List<UserProfileDto> getUserDtoList() {
        return userDtoList;
    }

    public void setUserDtoList(List<UserProfileDto> userDtoList) {
        this.userDtoList = userDtoList;
    }
}
