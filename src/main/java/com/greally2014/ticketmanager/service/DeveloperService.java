package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.DeveloperRepository;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    public List<UserProfileDto> findProfileDtoList() {
        return developerRepository.findAll().stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());
    }
}
