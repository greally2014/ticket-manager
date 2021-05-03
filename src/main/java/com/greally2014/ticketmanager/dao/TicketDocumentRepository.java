package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.TicketDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketDocumentRepository extends JpaRepository<TicketDocument, Long> {
}
