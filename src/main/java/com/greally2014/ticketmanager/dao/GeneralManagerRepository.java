package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.GeneralManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralManagerRepository extends JpaRepository<GeneralManager, Long> {
    GeneralManager findByUsername(String username);
}
