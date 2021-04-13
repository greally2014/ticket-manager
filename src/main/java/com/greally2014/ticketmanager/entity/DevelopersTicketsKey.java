package com.greally2014.ticketmanager.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DevelopersTicketsKey implements Serializable {

    @Column(name = "developer_id")
    private Long developerId;

    @Column(name = "ticket_id")
    private Long ticketId;

    public DevelopersTicketsKey(Long developerId, Long ticketId) {
        this.developerId = developerId;
        this.ticketId = ticketId;
    }

    public DevelopersTicketsKey() {
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DevelopersTicketsKey)) return false;
        DevelopersTicketsKey that = (DevelopersTicketsKey) o;
        return developerId.equals(that.developerId) &&
                ticketId.equals(that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(developerId, ticketId);
    }
}
