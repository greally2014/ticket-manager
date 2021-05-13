package com.greally2014.ticketmanager.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersProjectsKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "project_id")
    private Long projectId;

    public UsersProjectsKey(Long userId, Long projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }

    public UsersProjectsKey() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long courseId) {
        this.projectId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersProjectsKey)) return false;
        UsersProjectsKey that = (UsersProjectsKey) o;
        return userId.equals(that.userId) &&
                projectId.equals(that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId);
    }
}
