package com.greally2014.ticketmanager.exception;

public class EmailNotFoundException extends Exception {
    public EmailNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
