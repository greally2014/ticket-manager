package com.greally2014.ticketmanager.dto.ticket;

import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.TicketActivity;

import java.util.List;

public class TicketDetailsDto {

    private TicketDto ticketDto;

    private List<UserProfileDto> developerDtoList;

    private List<TicketCommentsDto> ticketCommentsDtoList;

    private List<TicketDocumentDto> ticketDocumentDtoList;

    private List<TicketActivity> ticketActivities;

    public TicketDetailsDto(TicketDto ticketDto,
                            List<UserProfileDto> developerDtoList,
                            List<TicketCommentsDto> ticketCommentsDtoList,
                            List<TicketDocumentDto> ticketDocumentDtoList,
                            List<TicketActivity> ticketActivities) {
        this.ticketDto = ticketDto;
        this.developerDtoList = developerDtoList;
        this.ticketCommentsDtoList = ticketCommentsDtoList;
        this.ticketDocumentDtoList = ticketDocumentDtoList;
        this.ticketActivities = ticketActivities;
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

    public List<TicketDocumentDto> getTicketDocumentDtoList() {
        return ticketDocumentDtoList;
    }

    public void setTicketDocumentDtoList(List<TicketDocumentDto> ticketDocumentDtoList) {
        this.ticketDocumentDtoList = ticketDocumentDtoList;
    }

    public List<TicketActivity> getTicketActivities() {
        return ticketActivities;
    }

    public void setTicketActivities(List<TicketActivity> ticketActivities) {
        this.ticketActivities = ticketActivities;
    }
}
