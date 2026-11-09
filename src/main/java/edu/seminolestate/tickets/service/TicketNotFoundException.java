package edu.seminolestate.tickets.service;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(long id) {
        super("Ticket not found: " + id);
    }
}
