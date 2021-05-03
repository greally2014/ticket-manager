package com.greally2014.ticketmanager.dto.ticket;

import com.greally2014.ticketmanager.entity.TicketComments;
import com.greally2014.ticketmanager.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketCommentsDto {

    public static final String DATE_FORMATTER = "dd-mm-yyyy hh:mm";

    private User user;

    private String comment;

    private LocalDateTime dateTimePosted;

    public TicketCommentsDto(TicketComments ticketComments) {
        this.user = ticketComments.getUser();
        this.comment = ticketComments.getComment();
        this.dateTimePosted = ticketComments.getDateTimePosted();
    }

    public TicketCommentsDto() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateTimePosted() {
        return dateTimePosted;
    }

    public void setDateTimePosted(LocalDateTime dateTimePosted) {
        this.dateTimePosted = dateTimePosted;
    }

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return dateTimePosted.format(formatter);
    }
}
