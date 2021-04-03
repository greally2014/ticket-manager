package com.greally2014.ticketmanager.formModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class FormProject {

    private Long id;

    @NotNull(message = "Title cannot be null")
    @Size(min = )
    private String title;

    private String description;

    private Date dateCreated;

}
