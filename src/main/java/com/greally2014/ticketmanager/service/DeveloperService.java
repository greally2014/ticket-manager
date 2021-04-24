package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.DeveloperRepository;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.DevelopersTickets;
import com.greally2014.ticketmanager.entity.Ticket;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    @Transactional
    public List<UserProfileDto> findProfileDtoList() {
        return developerRepository.findAll().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Ticket> findTickets(String username) {
        return developerRepository.findByUsername(username).getDevelopersTickets().stream()
                .map(DevelopersTickets::getTicket)
                .collect(Collectors.toList());
    }
}
