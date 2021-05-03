package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_documents")
public class TicketDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @Column(name = "content")
    private byte[] content;

    @Column(name = "datetime_posted")
    private LocalDateTime dateTimePosted;

    public TicketDocument(String name, Long size, byte[] content, LocalDateTime localDateTime) {
        this.name = name;
        this.size = size;
        this.content = content;
        this.dateTimePosted = localDateTime;
    }

    public TicketDocument() {
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public LocalDateTime getDateTimePosted() {
        return dateTimePosted;
    }

    public void setDateTimePosted(LocalDateTime dateTimePosted) {
        this.dateTimePosted = dateTimePosted;
    }

}
