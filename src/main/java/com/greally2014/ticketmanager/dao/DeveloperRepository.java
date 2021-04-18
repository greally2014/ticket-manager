package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
}
