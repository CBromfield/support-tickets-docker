package edu.seminolestate.tickets.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(TicketRepository.class)
class TicketRepositorySecurityTest {
    @Autowired TicketRepository repo;

    @Test void sqlInjectionPayloadIsTreatedAsSearchText(){
        int total=repo.findAll().size();
        var results=repo.search("' OR 1=1 --");
        assertTrue(total > 0);
        assertTrue(results.isEmpty(),"Injection text must not turn the WHERE clause into a match-all query");
    }

    @Test void wildcardCharactersAreTreatedLiterally(){
        assertTrue(repo.search("%").isEmpty());
        assertTrue(repo.search("_").isEmpty());
    }
}
