package edu.seminolestate.tickets.model;
public record Ticket(Long id, String requesterName, String email, String category, String description, TicketStatus status, String accessToken) {}
