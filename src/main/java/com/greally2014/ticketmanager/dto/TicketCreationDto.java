package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.Project;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TicketCreationDto {

    @Valid
    private TicketDto ticketDto;

    @NotNull(message = "Please select a project")
    private Long projectId;

    private List<Project> projectList;

    public TicketCreationDto(TicketDto ticketDto, Long projectId, List<Project> projectList) {
        this.ticketDto = ticketDto;
        this.projectId = projectId;
        this.projectList = projectList;
    }

    public TicketCreationDto(TicketDto ticketDto, List<Project> projectList) {
        this.ticketDto = ticketDto;
        this.projectList = projectList;
    }

    public TicketCreationDto() {
    }

    public TicketDto getTicketDto() {
        return ticketDto;
    }

    public void setTicketDto(TicketDto ticketDto) {
        this.ticketDto = ticketDto;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
