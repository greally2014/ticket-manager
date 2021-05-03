package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.ticket.*;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.TicketDocument;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.TicketDocumentNotFoundException;
import com.greally2014.ticketmanager.exception.TicketNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.service.SubmitterService;
import com.greally2014.ticketmanager.service.TicketService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
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
    public String listAllTickets(Model model, Principal principal) {
        model.addAttribute("tickets",
                ticketService.findAllByRole(principal.getName())
        );

        return "ticket/ticket-list";
    }

    @GetMapping("/showAddForm")
    @PreAuthorize("hasRole('SUBMITTER')")
    public String showAddTicketForm(Model model) {
        model.addAttribute("ticketCreationDto", ticketService.getCreationDto());

        return "ticket/ticket-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('SUBMITTER')")
    public String addTicket(@ModelAttribute("ticketCreationDto") @Valid TicketCreationDto ticketCreationDto,
                            BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            ticketCreationDto.setProjectList(submitterService.findProjects(principal.getName()));

            return "ticket/ticket-add";

        } else {
            try {
                ticketService.add(ticketCreationDto);

                return "redirect:/tickets/listAll";

            } catch (ProjectNotFoundException e) {

                return "redirect:/tickets/showAddForm";
            }
        }
    }

    @GetMapping("/showDetailsPage")
    public String showTicketDetailsPage(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("ticketDetailsDto", ticketService.getDetailsDto(id));
            model.addAttribute("ticketCommentsCreationDto", new TicketCommentsCreationDto(id));
            model.addAttribute("ticketDocumentUploadDto", new TicketDocumentUploadDto(id));

            return "ticket/ticket-details";

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }
    }

    @GetMapping("/showUpdateFieldsForm")
    @PreAuthorize("hasRole('DEVELOPER')")
    public String showUpdateTicketFieldsForm(@RequestParam("id") Long id, Model model) {
        try {
            model.addAttribute("ticketDto", ticketService.getDto(id));

            return "ticket/ticket-update-fields";

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }
    }

    @PostMapping("/updateFields")
    @PreAuthorize("hasRole('DEVELOPER')")
    public String updateTicketFields(@ModelAttribute("ticketDto") @Valid TicketDto ticketDto,
                                     BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            return "ticket/ticket-update-fields";

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
    @PreAuthorize("hasRole('DEVELOPER')")
    public String closeTicket(@RequestParam("id") Long id) {
        try {
            ticketService.delete(id);

        } catch (TicketNotFoundException e) {
            //nothing
        }

        return "redirect:/tickets/listAll";
    }

    @GetMapping("/showAddDeveloperForm")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public String showAddTicketDeveloperForm(@RequestParam("id") Long id, Model model) {
        try {
            TicketDto ticketDto = ticketService.getDto(id);
            List<UserProfileDto> userDtoList =
                    ticketService.findAllDeveloperDtoNotAdded(id);
            TicketAddDeveloperDto ticketAddDeveloperDto = new TicketAddDeveloperDto(ticketDto, userDtoList);

            model.addAttribute("ticketAddDeveloperDto", ticketAddDeveloperDto);

            return "ticket/ticket-add-developer";

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }
    }

    @PostMapping("/addDeveloper")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public String addTicketDeveloper(@ModelAttribute("ticketAddDeveloperDto") @Valid TicketAddDeveloperDto ticketAddDeveloperDto,
                                     BindingResult bindingResult, Model model) {
        Long ticketId = ticketAddDeveloperDto.getTicketDto().getId();

        if (bindingResult.hasErrors()) {
            try {
                ticketAddDeveloperDto.setDeveloperDtoList(ticketService.findAllDeveloperDtoNotAdded(ticketId));

                return "ticket/ticket-add-developer";

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }

        } else {
            try {
                ticketService.addDevelopers(ticketAddDeveloperDto);
                ticketService.setStatus(ticketId);

                return showTicketDetailsPage(ticketId, model);

            } catch (UserNotFoundException e) {

                return showAddTicketDeveloperForm(ticketId, model);

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }

        }
    }

    @GetMapping("/kickDeveloper")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public String kickTicketDeveloper(@RequestParam("developerId") Long developerId,
                                      @RequestParam("ticketId") Long ticketId,
                                      Model model) {
        try {
            ticketService.kickDeveloper(developerId, ticketId);
            ticketService.setStatus(ticketId);

        } catch (UserNotFoundException e) {
            // nothing

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }

        return showTicketDetailsPage(ticketId, model);
    }

    @GetMapping("/showDeveloperDetails")
    @PreAuthorize("hasAnyRole('GENERAL_MANAGER', 'PROJECT_MANAGER')")
    public String showDeveloperDetailsPage(@RequestParam("developerId") Long developerId,
                                           @RequestParam("ticketId") Long ticketId,
                                           Model model) {
        try {
            model.addAttribute("employee", customUserDetailsService.findById(developerId));
            model.addAttribute("ticketId", ticketId);

            return "ticket/ticket-employee-details";

        } catch (UserNotFoundException e) {

            return showTicketDetailsPage(ticketId, model);
        }
    }

    @PostMapping("/addComment")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'DEVELOPER', 'SUBMITTER')")
    public String addTicketComment(@ModelAttribute("ticketCommentsCreationDto") @Valid TicketCommentsCreationDto ticketCommentsCreationDto,
                                   BindingResult bindingResult, Model model, Principal principal) {
        Long ticketId = ticketCommentsCreationDto.getTicketId();

        if (bindingResult.hasErrors()) {
            try {
                model.addAttribute("ticketDetailsDto", ticketService.getDetailsDto(ticketId));
                model.addAttribute("ticketCommentsCreationDto", ticketCommentsCreationDto);
                model.addAttribute("ticketDocumentUploadDto", new TicketDocumentUploadDto(ticketId));

                return "ticket/ticket-details";

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }

        } else {
            User user = customUserDetailsService.loadUserByUsername(principal.getName()).getUser();
            String comment = ticketCommentsCreationDto.getComment();

            try {
                ticketService.addComment(user, ticketId, comment);

                return showTicketDetailsPage(ticketId, model);

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }
        }
    }

    @GetMapping("/deleteComments")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public String deleteTicketComments(@RequestParam("ticketId") Long ticketId, Model model) {
        try {
            ticketService.deleteComments(ticketId);

            return showTicketDetailsPage(ticketId, model);

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";
        }
    }

    @PostMapping("uploadDocument")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'DEVELOPER', 'SUBMITTER')")
    public String uploadTicketDocument(@ModelAttribute("ticketDocumentUploadDto") @Valid TicketDocumentUploadDto ticketDocumentUploadDto,
                                       BindingResult bindingResult, Model model) throws IOException {
        Long ticketId = ticketDocumentUploadDto.getTicketId();

        if (bindingResult.hasErrors()) {
            try {
                model.addAttribute("ticketDetailsDto", ticketService.getDetailsDto(ticketId));
                model.addAttribute("ticketCommentsCreationDto", new TicketCommentsCreationDto(ticketId));
                model.addAttribute("ticketDocumentUploadDto", ticketDocumentUploadDto);

                return "ticket/ticket-details";

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }

        } else {
            try {
                MultipartFile document = ticketDocumentUploadDto.getDocument();
                ticketService.addDocument(ticketId, document);

                return showTicketDetailsPage(ticketId, model);

            } catch (TicketNotFoundException e) {

                return "redirect:/tickets/listAll";
            }
        }
    }

    @GetMapping("deleteDocument")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'DEVELOPER', 'SUBMITTER')")
    public String deleteTicketDocument(@RequestParam("ticketDocumentId") Long ticketDocumentId,
                                       @RequestParam("ticketId") Long ticketId,
                                       Model model) {
        try {
            ticketService.deleteDocument(ticketId, ticketDocumentId);

        } catch (TicketNotFoundException e) {

            return "redirect:/tickets/listAll";

        } catch (TicketDocumentNotFoundException e) {
            //nothing
        }

        return showTicketDetailsPage(ticketId, model);

    }

    @GetMapping("downloadDocument")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'DEVELOPER', 'SUBMITTER')")
    public void downloadTicketDocument(@RequestParam("ticketDocumentId") Long ticketDocumentId,
                                       @RequestParam("ticketId") Long ticketId,
                                       HttpServletResponse response,
                                       Principal principal,
                                       Model model) throws IOException {
        try {
            TicketDocument ticketDocument = ticketService.findDocument(ticketId, ticketDocumentId);

            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + ticketDocument.getName();

            response.setHeader(headerKey, headerValue);

            ServletOutputStream outputStream = response.getOutputStream();

            outputStream.write(ticketDocument.getContent());
            outputStream.close();


        } catch (TicketNotFoundException e) {
            listAllTickets(model, principal);
            e.printStackTrace();

        } catch (TicketDocumentNotFoundException e) {
            showTicketDetailsPage(ticketId, model);
            e.printStackTrace();
        }
    }
}
