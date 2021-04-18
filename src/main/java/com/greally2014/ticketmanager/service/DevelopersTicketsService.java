package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.DevelopersTicketsRepository;
import com.greally2014.ticketmanager.entity.DevelopersTickets;
import org.springframework.stereotype.Service;

@Service
public class DevelopersTicketsService {

    private final DevelopersTicketsRepository developersTicketsRepository;

    public DevelopersTicketsService(DevelopersTicketsRepository developersTicketsRepository) {
        this.developersTicketsRepository = developersTicketsRepository;
    }

    public DevelopersTickets findByDeveloperIdAndTicketId(Long userId, Long ticketId) {
        return developersTicketsRepository.findByDeveloperIdAndTicketId(userId, ticketId);
    }

}
