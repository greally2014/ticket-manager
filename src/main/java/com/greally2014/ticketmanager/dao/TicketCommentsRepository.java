package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.TicketComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentsRepository extends JpaRepository<TicketComments, Long> {
    void deleteByTicketId(Long ticketId);
}
