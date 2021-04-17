package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.Ticket;
import com.greally2014.ticketmanager.validation.selected.Selected;

import javax.validation.Valid;
import java.util.List;

public class ProjectDetailsDto {

    @Valid
    private ProjectDto projectDto;

    @Selected
    private List<UserProfileDto> projectManagerDtoList;

    @Selected
    private List<UserProfileDto> developerDtoList;

    @Selected
    private List<UserProfileDto> submitterDtoList;

    private List<Ticket> ticketList;

    public ProjectDetailsDto(ProjectDto projectDto, List<UserProfileDto> projectManagerDtoList) {
        this.projectDto = projectDto;
        this.projectManagerDtoList = projectManagerDtoList;
    }

    public ProjectDetailsDto(ProjectDto projectDto,
                             List<UserProfileDto> projectManagerDtoList, List<UserProfileDto> developerDtoList,
                             List<UserProfileDto> submitterDtoList, List<Ticket> ticketList) {
        this.projectDto = projectDto;
        this.projectManagerDtoList = projectManagerDtoList;
        this.developerDtoList = developerDtoList;
        this.submitterDtoList = submitterDtoList;
        this.ticketList = ticketList;
    }

    public ProjectDetailsDto() {
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

    public List<UserProfileDto> getDeveloperDtoList() {
        return developerDtoList;
    }

    public void setDeveloperDtoList(List<UserProfileDto> developerDtoList) {
        this.developerDtoList = developerDtoList;
    }

    public List<UserProfileDto> getSubmitterDtoList() {
        return submitterDtoList;
    }

    public void setSubmitterDtoList(List<UserProfileDto> submitterDtoList) {
        this.submitterDtoList = submitterDtoList;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
