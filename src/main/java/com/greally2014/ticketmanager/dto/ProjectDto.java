package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.GeneralManager;
import com.greally2014.ticketmanager.entity.Project;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class ProjectDto {

    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 50, message = "Title is required")
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 200, message = "Description is required")
    private String description;

    private LocalDate dateCreated;

    private GeneralManager creator;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.dateCreated = project.getDateCreated();
        this.creator = project.getCreator();
    }

    public ProjectDto() {
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

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public GeneralManager getCreator() {
        return creator;
    }

    public void setCreator(GeneralManager creator) {
        this.creator = creator;
    }
}
