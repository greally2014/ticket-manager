package com.greally2014.ticketmanager.dto.ticket;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TicketCommentsCreationDto {

    private Long ticketId;

    @NotNull(message = "Please enter a comment")
    @Size(message = "Please enter a comment")
    private String comment;

    public TicketCommentsCreationDto(Long ticketId, String comment) {
        this.ticketId = ticketId;
        this.comment = comment;
    }

    public TicketCommentsCreationDto(Long ticketId) {
        this.ticketId = ticketId;
    }

    public TicketCommentsCreationDto() {
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
