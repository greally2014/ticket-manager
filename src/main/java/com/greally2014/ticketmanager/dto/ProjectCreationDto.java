package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.validation.selected.Selected;

import javax.validation.Valid;
import java.util.List;

public class ProjectCreationDto {

    @Valid
    private ProjectDto projectDto;

    @Selected
    private List<UserProfileDto> projectManagerDtoList;

    public ProjectCreationDto(ProjectDto projectDto, List<UserProfileDto> projectManagerDtoList) {
        this.projectDto = projectDto;
        this.projectManagerDtoList = projectManagerDtoList;
    }

    public ProjectCreationDto() {
    }

    public ProjectDto getProjectDto() {
        return projectDto;
    }

    public void setProjectDto(ProjectDto projectDto) {
        this.projectDto = projectDto;
    }

    public List<UserProfileDto> getProjectManagerDtoList() {
        return projectManagerDtoList;
    }

    public void setProjectManagerDtoList(List<UserProfileDto> projectManagerDtoList) {
        this.projectManagerDtoList = projectManagerDtoList;
    }
}
