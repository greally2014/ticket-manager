package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.TicketRepository;
import com.greally2014.ticketmanager.dto.TicketCreationDto;
import com.greally2014.ticketmanager.dto.TicketDetailsDto;
import com.greally2014.ticketmanager.dto.TicketDto;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.TicketNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final CustomUserDetailsService customUserDetailsService;

    private final ProjectManagerService projectManagerService;

    private final DeveloperService developerService;

    private final SubmitterService submitterService;

    private final ProjectService projectService;

    private final DevelopersTicketsService developersTicketsService;

    private final TicketRepository ticketRepository;


    public TicketService(CustomUserDetailsService customUserDetailsService,
                         ProjectManagerService projectManagerService,
                         DeveloperService developerService,
                         SubmitterService submitterService,
                         DevelopersTicketsService developersTicketsService,
                         ProjectService projectService,
                         TicketRepository ticketRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.projectManagerService = projectManagerService;
        this.developerService = developerService;
        this.submitterService = submitterService;
        this.developersTicketsService = developersTicketsService;
        this.projectService = projectService;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket findById(Long id) throws TicketNotFoundException {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        ticketOptional.orElseThrow(() -> new TicketNotFoundException("Not found: " + id));
        return ticketOptional.get();
    }

    @Transactional
    public TicketDto getDto(Long id) throws TicketNotFoundException {
        try {
            return new TicketDto(findById(id));

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public List<Developer> getDevelopers(Long id) throws TicketNotFoundException {
        try {
            Ticket ticket = findById(id);
            return ticket.getDevelopersTickets().stream()
                    .map(DevelopersTickets::getDeveloper)
                    .collect(Collectors.toList());

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public List<Ticket> findAllByUsernameOrderByTitle(String username) {

        List<Ticket> tickets;

        try {
            User user = customUserDetailsService.loadUserByUsername(username).getUser();

            if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_PROJECT_MANAGER"))) {
                tickets = projectManagerService.findTickets(username);
            } else if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_DEVELOPER"))) {
                tickets = developerService.findTickets(username);
            } else if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_SUBMITTER"))) {
                tickets = submitterService.findTickets(username);
            } else {
                tickets = ticketRepository.findAll();
            }

            tickets.sort(Comparator.comparing(Ticket::getTitle));

            return tickets;

        } catch (UsernameNotFoundException e) {
            e.printStackTrace();

            throw e;
        }

    }

    @Transactional
    public void add(TicketCreationDto ticketCreationDto) throws ProjectNotFoundException {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        try {
            Project project = projectService.findById(ticketCreationDto.getProjectId());

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }

        Ticket ticket = new Ticket(
                ticketCreationDto.getTicketDto().getTitle(),
                ticketCreationDto.getTicketDto().getDescription(),
                ticketCreationDto.getTicketDto().getType(),
                "Unassigned",
                ticketCreationDto.getTicketDto().getPriority(),
                ticketCreationDto.getTicketDto().getDateCreated(),
                projectService.findById(ticketCreationDto.getProjectId()),
                (Submitter) customUserDetailsService.loadUserByUsername(principal.getName()).getUser()
        );

        ticketRepository.save(ticket);
    }

    @Transactional
    public TicketCreationDto getCreationDto() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        List<Project> projects = submitterService.findProjects(principal.getName());

        return new TicketCreationDto(new TicketDto(), projects);
    }

    @Transactional
    public List<UserProfileDto> findAllDeveloperProfileDto(Long id) throws TicketNotFoundException {
        try {
            List<UserProfileDto> userProfileDtoList = getDevelopers(id).stream()
                    .map(UserProfileDto::new)
                    .collect(Collectors.toList());

            userProfileDtoList.forEach(o -> o.setDevelopersTickets(
                    developersTicketsService.findByDeveloperIdAndTicketId(o.getId(), id)));

            return userProfileDtoList;

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            throw e;
        }

    }

    @Transactional
    public TicketDetailsDto getDetailsDto(Long id) throws TicketNotFoundException {
        try {

            return new TicketDetailsDto(
                    getDto(id),
                    findAllDeveloperProfileDto(id)
            );

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public void updateFields(TicketDto ticketDto) throws TicketNotFoundException {
        Ticket ticket = findById(ticketDto.getId());
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setType(ticketDto.getType());
        ticket.setPriority(ticketDto.getPriority());

        ticketRepository.save(ticket);
    }

    @Transactional
    public void delete(Long id) throws TicketNotFoundException {
        try {
            findById(id);
            ticketRepository.deleteById(id);

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }
}
