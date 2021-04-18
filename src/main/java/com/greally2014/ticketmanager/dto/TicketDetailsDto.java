package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.Developer;

import java.util.List;

public class TicketDetailsDto {

    private TicketDto ticketDto;

    private List<UserProfileDto> developerDtoList;

    public TicketDetailsDto(TicketDto ticketDto, List<UserProfileDto> developerDtoList) {
        this.ticketDto = ticketDto;
        this.developerDtoList = developerDtoList;
    }

    public TicketDetailsDto() {
    }

    public TicketDto getTicketDto() {
        return ticketDto;
    }

    public void setTicketDto(TicketDto ticketDto) {
        this.ticketDto = ticketDto;
    }

    public List<UserProfileDto> getDeveloperDtoList() {
        return developerDtoList;
    }

    public void setDeveloperDtoList(List<UserProfileDto> developerDtoList) {
        this.developerDtoList = developerDtoList;
    }
}
