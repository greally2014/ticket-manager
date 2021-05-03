package com.greally2014.ticketmanager.dto.ticket;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class TicketDocumentUploadDto {

    @NotNull(message = "File is required")
    private MultipartFile document;

    private Long ticketId;

    public TicketDocumentUploadDto(Long ticketId) {
        this.ticketId = ticketId;
    }

    public TicketDocumentUploadDto() {
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public MultipartFile getDocument() {
        return document;
    }

    public void setDocument(MultipartFile document) {
        this.document = document;
    }
}
