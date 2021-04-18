package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.Submitter;
import com.greally2014.ticketmanager.entity.Ticket;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class TicketDto {

    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 50, message = "Title is required")
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 200, message = "Description is required")
    private String description;

    @NotNull(message = "Please select a type")
    @Size(min = 1, max = 50, message = "Please select a type")
    private String type;

    private String status;

    @NotNull(message = "Please select a priority")
    @Size(min = 1, max = 6, message = "Please select a priority")
    private String priority;

    private LocalDate dateCreated;

    private Submitter submitter;

    private Project project;

    public TicketDto(Ticket ticket) {
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.description = ticket.getDescription();
        this.type = ticket.getType();
        this.status = ticket.getStatus();
        this.priority = ticket.getPriority();
        this.dateCreated = ticket.getDateCreated();
        this.submitter = ticket.getSubmitter();
        this.project = ticket.getProject();
    }

    public TicketDto() {
        this.dateCreated = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Submitter getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Submitter submitter) {
        this.submitter = submitter;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
