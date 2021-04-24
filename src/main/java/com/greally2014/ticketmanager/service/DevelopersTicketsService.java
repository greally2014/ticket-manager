package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.DevelopersTicketsRepository;
import com.greally2014.ticketmanager.entity.Developer;
import com.greally2014.ticketmanager.entity.DevelopersTickets;
import com.greally2014.ticketmanager.entity.Ticket;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DevelopersTicketsService {

    private final DevelopersTicketsRepository developersTicketsRepository;

    public DevelopersTicketsService(DevelopersTicketsRepository developersTicketsRepository) {
        this.developersTicketsRepository = developersTicketsRepository;
    }

    @Transactional
    public DevelopersTickets findByDeveloperIdAndTicketId(Long userId, Long ticketId) {
        return developersTicketsRepository.findByDeveloperIdAndTicketId(userId, ticketId);
    }

    @Transactional
    public void deleteByDeveloperIdAndTicketId(Long developerId, Long ticketId) {
        developersTicketsRepository.deleteByDeveloperIdAndTicketId(developerId, ticketId);
    }

    @Transactional
    public void add(Developer developer, Ticket ticket) {
        DevelopersTickets developersTickets = new DevelopersTickets(developer, ticket);
        developersTicketsRepository.save(developersTickets);
    }
}
