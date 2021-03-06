package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.TicketCommentsRepository;
import com.greally2014.ticketmanager.entity.Ticket;
import com.greally2014.ticketmanager.entity.TicketComments;
import com.greally2014.ticketmanager.entity.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class TicketCommentsService {

    private final TicketCommentsRepository ticketCommentsRepository;

    public TicketCommentsService(TicketCommentsRepository ticketCommentsRepository) {
        this.ticketCommentsRepository = ticketCommentsRepository;
    }

    @Transactional
    public void add(User user, Ticket ticket, String comment) {
        ticketCommentsRepository.save(new TicketComments(user, ticket, comment, LocalDateTime.now()));
    }

    @Transactional
    public void delete(Long ticketId) {
        ticketCommentsRepository.deleteByTicketId(ticketId);
    }
}
