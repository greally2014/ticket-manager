package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.TicketNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.service.SubmitterService;
import com.greally2014.ticketmanager.service.TicketService;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private final CustomUserDetailsService customUserDetailsService;

    private final TicketService ticketService;

    private final SubmitterService submitterService;

    public TicketController(CustomUserDetailsService customUserDetailsService,
                            TicketService ticketService,
                            SubmitterService submitterService) {
        this.customUserDetailsService = customUserDetailsService;
        this.ticketService = ticketService;
        this.submitterService = submitterService;
    }

    @GetMapping("/listAll")
    public String listAllTickets(Model model) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("tickets",
                ticketService.findAllByUsernameOrderByTitle(principal.getName())
        );

        return "ticket-list";
    }

    @GetMapping("/showAddForm")
    @PreAuthorize("hasRole('SUBMITTER')")
    public String showAddTicketForm(Model model) {
        // check if username exists and handle exception / have denied access redirect / error handler
        model.addAttribute("ticketCreationDto", ticketService.getCreationDto());

        return "ticket-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('SUBMITTER')")
    public String addTicket(@ModelAttribute("ticketCreationDto") @Valid TicketCreationDto ticketCreationDto,
                            BindingResult bindingResult) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        if (bindingResult.hasErrors()) {
            ticketCreationDto.setProjectList(submitterService.findProjects(principal.getName()));

            return "ticket-add";

        } else {
            try {
                ticketService.add(ticketCreationDto);

                return "redirect:/tickets/listAll";

            } catch (ProjectNotFoundException e) {

                return "ticket-add";
            }

        }
    }

    @GetMapping("/showDetailsPage")
    public String showTicketDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            TicketCommentsCreationDto ticketCommentsCreationDto = new TicketCommentsCreationDto();
            ticketCommentsCreationDto.setTicketId(id);

            model.addAttribute("ticketDetailsDto", ticketService.getDetailsDto(id));
            model.addAttribute("ticketCommentsCreationDto", ticketCommentsCreationDto);

            return "ticket-details";

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }
    }

    @GetMapping("/showDeveloperDetails")
    public String showDeveloperDetailsPage(@RequestParam("developerId") Long developerId,
                                           @RequestParam("ticketId") Long ticketId,
                                           Model model) {
        try {
            model.addAttribute("employee", customUserDetailsService.findById(developerId));
            model.addAttribute("ticketId", ticketId);

            return "ticket-employee-details";

        } catch (UserNotFoundException e) {
            e.printStackTrace();

            return showTicketDetailsPage(ticketId, model);
        }
    }

    @GetMapping("/showUpdateFieldsForm")
    public String showUpdateTicketFieldsForm(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("ticketDto", ticketService.getDto(id));

            return "ticket-update-fields";

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            return "redirect:/tickets/listAll";
        }
    }

    @PostMapping("/updateFields")
    public String updateTicketFields(@ModelAttribute("ticketDto") @Valid TicketDto ticketDto,
                                     BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {

            return "ticket-update-fields";

        } else {
            try {
                ticketService.updateFields(ticketDto);

                return showTicketDetailsPage(ticketDto.getId(), model);

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }
        }
    }

    @GetMapping("/close")
    public String closeTicket(@RequestParam("id") Long id) {
        try {
            ticketService.delete(id);

            return "redirect:/tickets/listAll";

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }
    }

    @GetMapping("/kickDeveloper")
    public String kickTicketDeveloper(@RequestParam("developerId") Long developerId,
                                      @RequestParam("ticketId") Long ticketId,
                                      Model model) {
        try {
            ticketService.kickUser(developerId, ticketId);
            ticketService.setStatus(ticketId);

        } catch (Exception e) {
            //nothing
        }

        return showTicketDetailsPage(ticketId, model);
    }

    @GetMapping("/showAddDeveloperForm")
    public String showAddTicketDeveloperForm(@RequestParam("id") Long id, Model model) {
        try {
            List<UserProfileDto> userDtoList =
                    ticketService.findAllDeveloperProfileDtoNotAdded(id);
            TicketDto ticketDto = ticketService.getDto(id);
            TicketAddDeveloperDto ticketAddDeveloperDto = new TicketAddDeveloperDto(ticketDto, userDtoList);

            model.addAttribute("ticketAddDeveloperDto", ticketAddDeveloperDto);

            return "ticket-add-developer";

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            return showTicketDetailsPage(id, model);
        }
    }

    @PostMapping("/addDeveloper")
    public String addTicketDeveloper(@ModelAttribute("ticketAddDeveloperDto") @Valid TicketAddDeveloperDto ticketAddDeveloperDto,
                                     BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            if (ticketAddDeveloperDto.getDeveloperDtoList() == null) {

                return "ticket-add-developer";

            } else {
                try {
                    List<UserProfileDto> userProfileDtos = ticketService.findAllDeveloperProfileDtoNotAdded(
                            ticketAddDeveloperDto.getTicketDto().getId()
                    );

                    ticketAddDeveloperDto.setDeveloperDtoList(userProfileDtos);

                    return "ticket-add-developer";

                } catch (TicketNotFoundException e) {
                    e.printStackTrace();

                    return "redirect:/tickets/listAll";
                }
            }

        } else {
            try {
                ticketService.addDevelopers(ticketAddDeveloperDto);
                ticketService.setStatus(ticketAddDeveloperDto.getTicketDto().getId());
                return showTicketDetailsPage(ticketAddDeveloperDto.getTicketDto().getId(), model);

            } catch (UserNotFoundException e) {
                e.printStackTrace();

                return showTicketDetailsPage(ticketAddDeveloperDto.getTicketDto().getId(), model);

            } catch (TicketNotFoundException e) {
                e.printStackTrace();

                return "redirect:/tickets/listAll";
            }

        }
    }

    @PostMapping("/addComment")
    public String addTicketComment(@ModelAttribute("ticketCommentsCreationDto") @Valid TicketCommentsCreationDto ticketCommentsCreationDto,
                                   BindingResult bindingResult, Model model) throws UserNotFoundException {
        if (bindingResult.hasErrors()) {

            return "ticket-details";

        } else {
            Long ticketId = ticketCommentsCreationDto.getTicketId();
            Long userId = customUserDetailsService.loadUserByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()).getUser().getId();
            String comment = ticketCommentsCreationDto.getComment();

            try {
                ticketService.addComment(userId, ticketId, comment);

                return showTicketDetailsPage(ticketId, model);

            } catch (TicketNotFoundException e) {
                e.printStackTrace();

                return "redirect:/tickets/listAll";
            }
        }
    }

    @GetMapping("/deleteComments")
    public String deleteTicketComments(@RequestParam("ticketId") Long ticketId, Model model) {
        try {
            ticketService.deleteComments(ticketId);

            return showTicketDetailsPage(ticketId, model);

        } catch (TicketNotFoundException e) {
            e.printStackTrace();

            return "redirect:/tickets/listAll";
        }
    }

}
