package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
