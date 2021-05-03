package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.TicketDocumentRepository;
import com.greally2014.ticketmanager.entity.Ticket;
import com.greally2014.ticketmanager.entity.TicketDocument;
import com.greally2014.ticketmanager.exception.TicketDocumentNotFoundException;
import com.greally2014.ticketmanager.exception.TicketNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketDocumentService {

    private TicketDocumentRepository ticketDocumentRepository;

    public TicketDocumentService(TicketDocumentRepository ticketDocumentRepository) {
        this.ticketDocumentRepository = ticketDocumentRepository;
    }

    public TicketDocument findById(Long id) throws TicketDocumentNotFoundException {
        Optional<TicketDocument> ticketDocumentOptional = ticketDocumentRepository.findById(id);
        ticketDocumentOptional.orElseThrow(() -> new TicketDocumentNotFoundException("Not found: " + id));

        return ticketDocumentOptional.get();
    }

    public void delete(Long id) throws TicketDocumentNotFoundException {
        TicketDocument ticketDocument = findById(id);
        ticketDocumentRepository.delete(ticketDocument);
    }
}
