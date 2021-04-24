package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
public class TicketComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "datetime_posted")
    private LocalDateTime dateTimePosted;

    @Column(name = "comment")
    private String comment;

    public TicketComments(User user, Ticket ticket, LocalDateTime dateTimePosted, String comment) {
        this.user = user;
        this.ticket = ticket;
        this.dateTimePosted = dateTimePosted;
        this.comment = comment;
    }

    public TicketComments(User user, Ticket ticket, String comment) {
        this.user = user;
        this.ticket = ticket;
        this.dateTimePosted = LocalDateTime.now();
        this.comment = comment;
    }

    public TicketComments() {
        this.dateTimePosted = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public LocalDateTime getDateTimePosted() {
        return dateTimePosted;
    }

    public void setDateTimePosted(LocalDateTime dateTimePosted) {
        this.dateTimePosted = dateTimePosted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
