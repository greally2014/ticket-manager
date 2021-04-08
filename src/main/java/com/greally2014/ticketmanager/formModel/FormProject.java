package com.greally2014.ticketmanager.formModel;

import com.greally2014.ticketmanager.entity.Project;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class FormProject {

    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 50, message = "Invalid format")
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 50, message = "Invalid format")
    private String description;

    private LocalDate dateCreated;

    public FormProject(Project project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.dateCreated = project.getDateCreated();
    }

    public FormProject() {
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
}
