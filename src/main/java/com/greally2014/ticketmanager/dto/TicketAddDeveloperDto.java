package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.validation.selected.Selected;

import javax.validation.constraints.NotNull;
import java.util.List;

public class TicketAddDeveloperDto {

    private TicketDto ticketDto;

    @NotNull(message = "There are no users to add")
    @Selected(message = "Please select a user")
    private List<UserProfileDto> developerDtoList;

    public TicketAddDeveloperDto(TicketDto ticketDto, List<UserProfileDto> developerDtoList) {
        this.ticketDto = ticketDto;
        this.developerDtoList = developerDtoList;
    }

    public TicketAddDeveloperDto() {
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
