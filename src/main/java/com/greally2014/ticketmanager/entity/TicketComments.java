package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
public class TicketComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade= {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade= {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "datetime_posted")
    private LocalDateTime dateTimePosted;

    @Column(name = "comment")
    private String comment;

    public TicketComments(User user, Ticket ticket, String comment, LocalDateTime localDateTime) {
        this.user = user;
        this.ticket = ticket;
        this.comment = comment;
        this.dateTimePosted = localDateTime;
    }

    public TicketComments() {
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
