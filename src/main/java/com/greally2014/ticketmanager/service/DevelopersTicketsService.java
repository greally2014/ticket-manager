package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.DevelopersTicketsRepository;
import com.greally2014.ticketmanager.dao.TicketRepository;
import com.greally2014.ticketmanager.entity.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DevelopersTicketsService {

    private final DevelopersTicketsRepository developersTicketsRepository;

    private final TicketRepository ticketRepository;

    public DevelopersTicketsService(DevelopersTicketsRepository developersTicketsRepository,
                                    TicketRepository ticketRepository) {
        this.developersTicketsRepository = developersTicketsRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public DevelopersTickets find(Long userId, Long ticketId) {
        return developersTicketsRepository.findByDeveloperIdAndTicketId(userId, ticketId);
    }

    @Transactional
    public void delete(Long developerId, Long ticketId) {
        developersTicketsRepository.deleteByDeveloperIdAndTicketId(developerId, ticketId);
    }

    @Transactional
    public void add(Developer developer, Ticket ticket) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        TicketActivity ticketActivity = new TicketActivity(
                "User Assigned: " + developer.getUsername(),
                principal.getName(),
                "Developer",
                LocalDateTime.now()
        );

        ticket.addActivity(ticketActivity);
        ticketRepository.save(ticket);

        developersTicketsRepository.save(new DevelopersTickets(developer, ticket, LocalDate.now()));
    }
}
