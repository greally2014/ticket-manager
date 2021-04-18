package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.SubmitterRepository;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmitterService {

    private final SubmitterRepository submitterRepository;

    public SubmitterService(SubmitterRepository submitterRepository) {
        this.submitterRepository = submitterRepository;
    }

    public List<UserProfileDto> findProfileDtoList() {
        return submitterRepository.findAll().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());
    }
}
