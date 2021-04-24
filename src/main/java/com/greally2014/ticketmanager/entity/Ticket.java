package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "submitter_id")
    private Submitter submitter;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<DevelopersTickets> developersTickets;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketComments> ticketComments;

    public Ticket(String title, String description,
                  String type, String priority,
                  LocalDate dateCreated,
                  Project project, Submitter submitter) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.dateCreated = dateCreated;
        this.project = project;
        this.submitter = submitter;
    }

    public Ticket() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate date_created) {
        this.dateCreated = date_created;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Submitter getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Submitter submitter) {
        this.submitter = submitter;
    }

    public List<DevelopersTickets> getDevelopersTickets() {
        return developersTickets;
    }

    public void setDevelopersTickets(List<DevelopersTickets> developersTickets) {
        this.developersTickets = developersTickets;
    }

    public List<TicketComments> getTicketComments() {
        return ticketComments;
    }

    public void setTicketComments(List<TicketComments> ticketComments) {
        this.ticketComments = ticketComments;
    }
}
