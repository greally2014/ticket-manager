package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Submitter;
import com.greally2014.ticketmanager.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmitterRepository extends JpaRepository<Submitter, Long> {
    Submitter findByUsername(String username);
}
