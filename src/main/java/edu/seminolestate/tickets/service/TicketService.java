package edu.seminolestate.tickets.service;

import edu.seminolestate.tickets.model.Ticket;
import edu.seminolestate.tickets.model.TicketStatus;
import edu.seminolestate.tickets.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Set<String> CATEGORIES = Set.of("Software", "Network", "Account", "Other");
    private static final int MAX_NAME = 100;
    private static final int MAX_EMAIL = 254;
    private static final int MAX_DESCRIPTION = 2000;
    private static final int MAX_SEARCH = 100;

    private final TicketRepository repo;

    public TicketService(TicketRepository repo) {
        this.repo = repo;
    }

    public List<Ticket> all() {
        return repo.findAll();
    }

    public Ticket get(long id) {
        if (id <= 0) throw new IllegalArgumentException("Ticket id must be positive");
        return repo.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    public List<Ticket> search(String q) {
        String term = q == null ? "" : q.trim();
        if (term.length() > MAX_SEARCH) throw new IllegalArgumentException("Search term is too long");
        return term.isEmpty() ? repo.findAll() : repo.search(term);
    }

    public Ticket create(String name, String email, String category, String description) {
        String safeName = required(name, "Requester name", MAX_NAME);
        String safeEmail = required(email, "Email", MAX_EMAIL).toLowerCase(Locale.ROOT);
        String safeCategory = required(category, "Category", 30);
        String safeDescription = required(description, "Description", MAX_DESCRIPTION);

        if (!EMAIL.matcher(safeEmail).matches()) throw new IllegalArgumentException("Email address is invalid");
        if (!CATEGORIES.contains(safeCategory)) throw new IllegalArgumentException("Category is invalid");

        String token = UUID.randomUUID().toString();
        Ticket ticket = repo.save(safeName, safeEmail, safeCategory, safeDescription, token);
        // Deliberately avoid logging the Ticket object: it contains email and an access token.
        log.info("Created support ticket id={} category={} status={}", ticket.id(), ticket.category(), ticket.status());
        return ticket;
    }

    public void changeStatus(long id, String status) {
        get(id); // verify the target exists before reporting success
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status is required");
        final TicketStatus parsed;
        try {
            parsed = TicketStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status is invalid");
        }
        repo.updateStatus(id, parsed);
    }

    private String required(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) throw new IllegalArgumentException(field + " is too long");
        return trimmed;
    }
}
