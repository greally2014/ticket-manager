package com.greally2014.ticketmanager.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Embeddable
public class TicketActivity {

    public static final String DATE_FORMATTER = "dd-mm-yyyy hh:mm";

    private String type;

    private String username;

    private String role;

    private LocalDateTime dateTimeCreated;

    public TicketActivity(String type, String username, String role, LocalDateTime dateTimeCreated) {
        this.type = type;
        this.username = username;
        this.role = role;
        this.dateTimeCreated = dateTimeCreated;
    }

    public TicketActivity() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    @Transient
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return dateTimeCreated.format(formatter);
    }
}
