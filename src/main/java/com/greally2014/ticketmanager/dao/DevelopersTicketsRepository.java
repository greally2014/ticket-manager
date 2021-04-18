package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.DevelopersTickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevelopersTicketsRepository extends JpaRepository<DevelopersTickets, Long> {

    void deleteByDeveloperIdAndTicketId(Long developerId, Long ticketId);

    DevelopersTickets findByDeveloperIdAndTicketId(Long developerId, Long ticketId);
}
