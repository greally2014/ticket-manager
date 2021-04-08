package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.formModel.FormProject;
import com.greally2014.ticketmanager.formModel.ProfileFormUser;
import com.greally2014.ticketmanager.validation.SelectedProjectManager;

import javax.validation.Valid;
import java.util.List;

public class ProjectCreationDto {

    @Valid
    private FormProject formProject;

    @SelectedProjectManager
    private List<ProfileFormUser> formProjectManagerList;

    public ProjectCreationDto(FormProject formProject, List<ProfileFormUser> formProjectManagerList) {
        this.formProject = formProject;
        this.formProjectManagerList = formProjectManagerList;
    }

    public ProjectCreationDto() {
    }

    public List<ProfileFormUser> getFormProjectManagerList() {
        return formProjectManagerList;
    }

    public void setFormProjectManagerList(List<ProfileFormUser> formProjectManagerList) {
        this.formProjectManagerList = formProjectManagerList;
    }

    public FormProject getFormProject() {
        return formProject;
    }

    public void setFormProject(FormProject formProject) {
        this.formProject = formProject;
    }
}
