package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Submitter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitterRepository extends JpaRepository<Submitter, Long> {
}
