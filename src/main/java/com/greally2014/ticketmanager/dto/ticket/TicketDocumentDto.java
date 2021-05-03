package com.greally2014.ticketmanager.dto.ticket;

import com.greally2014.ticketmanager.entity.TicketDocument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketDocumentDto {

    public static final String DATE_FORMATTER = "dd-mm-yyyy hh:mm";

    private Long id;

    private String name;

    private Long size;

    private LocalDateTime dateTimePosted;

    public TicketDocumentDto(TicketDocument ticketDocument) {
        this.id = ticketDocument.getId();
        this.name = ticketDocument.getName();
        this.size = ticketDocument.getSize();
        this.dateTimePosted = ticketDocument.getDateTimePosted();
    }

    public TicketDocumentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
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
