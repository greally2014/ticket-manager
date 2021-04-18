package com.greally2014.ticketmanager.exception;

import ch.qos.logback.core.encoder.EchoEncoder;

public class TicketNotFoundException extends Exception {
    public TicketNotFoundException(String message) {
        super(message);
    }
}
