package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.TicketRepository;
import com.greally2014.ticketmanager.dto.ticket.*;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.TicketDocumentNotFoundException;
import com.greally2014.ticketmanager.exception.TicketNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final CustomUserDetailsService customUserDetailsService;

    private final ProjectManagerService projectManagerService;

    private final DeveloperService developerService;

    private final SubmitterService submitterService;

    private final ProjectService projectService;

    private final DevelopersTicketsService developersTicketsService;

    private final TicketCommentsService ticketCommentsService;

    private final TicketDocumentService ticketDocumentService;

    private final TicketRepository ticketRepository;


    public TicketService(CustomUserDetailsService customUserDetailsService,
                         ProjectManagerService projectManagerService,
                         DeveloperService developerService,
                         SubmitterService submitterService,
                         DevelopersTicketsService developersTicketsService,
                         ProjectService projectService,
                         TicketCommentsService ticketCommentsService,
                         TicketDocumentService ticketDocumentService, TicketRepository ticketRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.projectManagerService = projectManagerService;
        this.developerService = developerService;
        this.submitterService = submitterService;
        this.developersTicketsService = developersTicketsService;
        this.projectService = projectService;
        this.ticketCommentsService = ticketCommentsService;
        this.ticketDocumentService = ticketDocumentService;
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

        return new TicketDto(findById(id));
    }

    @Transactional
    public List<Developer> getDevelopers(Long id) throws TicketNotFoundException {
        Ticket ticket = findById(id);

        return ticket.getDevelopersTickets().stream()
                .map(DevelopersTickets::getDeveloper)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Ticket> findAllByRole(String username) {
        User user = customUserDetailsService.loadUserByUsername(username).getUser();
        List<Ticket> tickets;

        if (customUserDetailsService.hasRole(user, "ROLE_PROJECT_MANAGER")) {
            tickets = projectManagerService.findTickets(username);

        } else if (customUserDetailsService.hasRole(user,"ROLE_DEVELOPER")) {
            tickets = developerService.findTickets(username);

        } else if (customUserDetailsService.hasRole(user, "ROLE_SUBMITTER")) {
            tickets = submitterService.findTickets(username);

        } else {
            tickets = ticketRepository.findAll();
        }

        tickets.sort(Comparator.comparing(Ticket::getTitle));

        return tickets;

    }

    @Transactional
    public void add(TicketCreationDto ticketCreationDto) throws ProjectNotFoundException {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = customUserDetailsService.loadUserByUsername(principal.getName()).getUser();

        Ticket ticket = new Ticket(
                ticketCreationDto.getTicketDto().getTitle(),
                ticketCreationDto.getTicketDto().getDescription(),
                ticketCreationDto.getTicketDto().getType(),
                ticketCreationDto.getTicketDto().getPriority(),
                ticketCreationDto.getTicketDto().getDateCreated(),
                projectService.findById(ticketCreationDto.getProjectId()),
                (Submitter) customUserDetailsService.loadUserByUsername(principal.getName()).getUser()
        );

        ticket.setStatus("Unassigned");

        TicketActivity ticketActivity = new TicketActivity(
                "Ticket Created",
                user.getUsername(),
                user.getRoleName(),
                LocalDateTime.now()
        );
        ticket.addActivity(ticketActivity);

        ticketRepository.save(ticket);
    }

    @Transactional
    public TicketCreationDto getCreationDto() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        List<Project> projects = submitterService.findProjects(principal.getName());

        return new TicketCreationDto(new TicketDto(), projects);
    }

    @Transactional
    public List<UserProfileDto> findAllDeveloperDto(Long id) throws TicketNotFoundException {
        List<UserProfileDto> developerDtoList = getDevelopers(id).stream()
                .map(UserProfileDto::new)
                .collect(Collectors.toList());

        developerDtoList.forEach(o -> o.setDevelopersTickets(developersTicketsService.find(o.getId(), id)));

        return developerDtoList;
    }

    @Transactional
    public TicketDetailsDto getDetailsDto(Long id) throws TicketNotFoundException {
        Ticket ticket = findById(id);

        return new TicketDetailsDto(
                getDto(id),
                findAllDeveloperDto(id),
                getCommentDtoList(id),
                getDocumentDtoList(id),
                ticket.getTicketActivities()
        );
    }

    @Transactional
    public void updateFields(TicketDto ticketDto) throws TicketNotFoundException {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = customUserDetailsService.loadUserByUsername(principal.getName()).getUser();

        Ticket ticket = findById(ticketDto.getId());
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setType(ticketDto.getType());
        ticket.setPriority(ticketDto.getPriority());

        TicketActivity ticketActivity = new TicketActivity(
                "Fields Updated",
                user.getUsername(),
                user.getRoleName(),
                LocalDateTime.now()
        );
        ticket.addActivity(ticketActivity);

        ticketRepository.save(ticket);
    }

    @Transactional
    public void delete(Long id) throws TicketNotFoundException {
        findById(id);
        ticketRepository.deleteById(id);
    }

    @Transactional
    public void kickDeveloper(Long developerId, Long ticketId) throws UserNotFoundException, TicketNotFoundException {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User principalUser = customUserDetailsService.loadUserByUsername(principal.getName()).getUser();
        User user = customUserDetailsService.findById(developerId);
        Ticket ticket = findById(ticketId);

        TicketActivity ticketActivity = new TicketActivity(
                "User Kicked: " + user.getUsername(),
                principal.getName(),
                principalUser.getRoleName(),
                LocalDateTime.now()
        );

        ticket.addActivity(ticketActivity);
        ticketRepository.save(ticket);

        developersTicketsService.delete(developerId, ticketId);

    }

    @Transactional
    public List<UserProfileDto> findAllDeveloperDtoNotAdded(Long id) throws TicketNotFoundException {
        List<UserProfileDto> alreadyAdded = getDetailsDto(id).getDeveloperDtoList();
        List<UserProfileDto> developerDtoList = developerService.findProfileDtoList();

        return projectService.removeAlreadyAdded(alreadyAdded, developerDtoList);
    }

    @Transactional
    public void addDevelopers(TicketAddDeveloperDto ticketAddDeveloperDto) throws UserNotFoundException, TicketNotFoundException {
        Ticket ticket = findById(ticketAddDeveloperDto.getTicketDto().getId());

        for (UserProfileDto userProfileDto : ticketAddDeveloperDto.getDeveloperDtoList()) {
            if (userProfileDto.getFlag()) {
                Developer developer = (Developer) customUserDetailsService.findById(userProfileDto.getId());
                developersTicketsService.add(developer, ticket);
            }
        }
    }

    @Transactional
    public void addComment(User user, Long ticketId, String comment) throws TicketNotFoundException {
        Ticket ticket = findById(ticketId);
        ticketCommentsService.add(user, ticket, comment);
    }

    @Transactional
    public void deleteComments(Long ticketId) throws TicketNotFoundException {
        findById(ticketId);
        ticketCommentsService.delete(ticketId);
    }

    @Transactional
    public void setStatus(Long ticketId) throws TicketNotFoundException {
        Ticket ticket = findById(ticketId);
        System.out.println(ticket.getDevelopersTickets());
        if (ticket.getDevelopersTickets() == null || ticket.getDevelopersTickets().isEmpty()) {
            ticket.setStatus("Unassigned");
        } else {
            ticket.setStatus("Occupied");
        }
    }

    @Transactional
    public List<TicketCommentsDto> getCommentDtoList(Long id) throws TicketNotFoundException {

        return findById(id).getTicketComments().stream()
                .map(TicketCommentsDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addDocument(Long ticketId, MultipartFile document) throws TicketNotFoundException, IOException {
        Ticket ticket = findById(ticketId);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(document.getOriginalFilename()));

        TicketDocument ticketDocument = new TicketDocument(
                fileName,
                document.getSize(),
                document.getBytes(),
                LocalDateTime.now()
        );

        ticket.addDocument(ticketDocument);
        ticketRepository.save(ticket);
    }

    @Transactional
    public List<TicketDocumentDto> getDocumentDtoList(Long id) throws TicketNotFoundException {

        return findById(id).getTicketDocuments().stream()
                .map(TicketDocumentDto:: new)
                .collect(Collectors.toList());
    }

    public void deleteDocument(Long ticketId, Long ticketDocumentId) throws TicketNotFoundException, TicketDocumentNotFoundException {
        Ticket ticket = findById(ticketId);
        ticketDocumentService.delete(ticketDocumentId);
    }

    public TicketDocument findDocument(Long ticketId, Long ticketDocumentId) throws TicketNotFoundException, TicketDocumentNotFoundException {
        Ticket ticket = findById(ticketId);
        return ticketDocumentService.findById(ticketDocumentId);
    }
}
