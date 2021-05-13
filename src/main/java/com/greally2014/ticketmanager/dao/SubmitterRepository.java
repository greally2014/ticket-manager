package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Submitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubmitterRepository extends JpaRepository<Submitter, Long> {
    Submitter findByUsername(String username);
}
