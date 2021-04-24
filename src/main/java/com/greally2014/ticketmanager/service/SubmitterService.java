package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.SubmitterRepository;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.Project;
import com.greally2014.ticketmanager.entity.Ticket;
import com.greally2014.ticketmanager.entity.UsersProjects;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmitterService {

    private final SubmitterRepository submitterRepository;

    public SubmitterService(SubmitterRepository submitterRepository) {
        this.submitterRepository = submitterRepository;
    }

    @Transactional
    public List<UserProfileDto> findProfileDtoList() {
        return submitterRepository.findAll().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Ticket> findTickets(String username) {
        return submitterRepository.findByUsername(username).getTickets();
    }

    @Transactional
    public List<Project> findProjects(String username) {
        return submitterRepository.findByUsername(username).getUsersProjects().stream()
                .map(UsersProjects::getProject)
                .collect(Collectors.toList());
    }
}
