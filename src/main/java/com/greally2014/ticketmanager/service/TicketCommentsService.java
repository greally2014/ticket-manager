package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.TicketCommentsRepository;
import com.greally2014.ticketmanager.entity.Ticket;
import com.greally2014.ticketmanager.entity.TicketComments;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.exception.TicketNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TicketCommentsService {

    private TicketCommentsRepository ticketCommentsRepository;

    public TicketCommentsService(TicketCommentsRepository ticketCommentsRepository) {
        this.ticketCommentsRepository = ticketCommentsRepository;
    }

    @Transactional
    public void add(User user, Ticket ticket, String comment) {
        TicketComments ticketComments = new TicketComments(user, ticket, comment);
        ticketCommentsRepository.save(ticketComments);
    }

    @Transactional
    public void deleteByTicketId(Long ticketId) {
        ticketCommentsRepository.deleteByTicketId(ticketId);
    }
}
