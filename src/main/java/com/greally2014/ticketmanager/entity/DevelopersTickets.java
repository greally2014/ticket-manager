package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "developers_tickets")
public class DevelopersTickets {

    @EmbeddedId
    private DevelopersTicketsKey id;

    @ManyToOne
    @MapsId(value = "developerId")
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @ManyToOne
    @MapsId(value = "ticketId")
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "date_assigned")
    private LocalDate dateAssigned;

    public DevelopersTickets(Developer developer, Ticket ticket, LocalDate dateAssigned) {
        this.id = new DevelopersTicketsKey();
        this.developer = developer;
        this.ticket = ticket;
        this.dateAssigned = dateAssigned;
    }

    public DevelopersTickets() {
    }

    public DevelopersTicketsKey getId() {
        return id;
    }

    public void setId(DevelopersTicketsKey id) {
        this.id = id;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public LocalDate getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDate dateAssigned) {
        this.dateAssigned = dateAssigned;
    }


}
