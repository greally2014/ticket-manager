package com.greally2014.ticketmanager.dto;

import java.util.List;

public class TicketDetailsDto {

    private TicketDto ticketDto;

    private List<UserProfileDto> developerDtoList;

    private List<TicketCommentsDto> ticketCommentsDtoList;

    public TicketDetailsDto(TicketDto ticketDto, List<UserProfileDto> developerDtoList, List<TicketCommentsDto> ticketCommentsDtoList) {
        this.ticketDto = ticketDto;
        this.developerDtoList = developerDtoList;
        this.ticketCommentsDtoList = ticketCommentsDtoList;
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

    public List<TicketCommentsDto> getTicketCommentsDtoList() {
        return ticketCommentsDtoList;
    }

    public void setTicketCommentsDtoList(List<TicketCommentsDto> ticketCommentsDtoList) {
        this.ticketCommentsDtoList = ticketCommentsDtoList;
    }
}
