package edu.seminolestate.tickets.service;

import edu.seminolestate.tickets.model.*;
import edu.seminolestate.tickets.repository.TicketRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TicketServiceTest {
    @Mock TicketRepository repo;
    @InjectMocks TicketService service;

    @Test void retrievesExistingTicket(){
        var t=new Ticket(1L,"Jordan Lee","jlee@example.edu","Software","IDE issue",TicketStatus.OPEN,"token");
        when(repo.findById(1L)).thenReturn(Optional.of(t));
        assertEquals("Jordan Lee",service.get(1L).requesterName());
    }

    @Test void missingTicketProducesNotFound(){
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TicketNotFoundException.class,()->service.get(99L));
    }

    @Test void changesTicketStatus(){
        when(repo.findById(2L)).thenReturn(Optional.of(ticket(2L)));
        service.changeStatus(2L,"CLOSED");
        verify(repo).updateStatus(2L,TicketStatus.CLOSED);
    }

    @ParameterizedTest
    @ValueSource(strings={"INVALID","closed-now","123"," "})
    void rejectsInvalidStatuses(String status){
        when(repo.findById(2L)).thenReturn(Optional.of(ticket(2L)));
        assertThrows(IllegalArgumentException.class,()->service.changeStatus(2L,status));
        verify(repo,never()).updateStatus(anyLong(),any());
    }

    @Test void searchesForKnownTicket(){
        when(repo.search("Wi-Fi")).thenReturn(List.of(ticket(2L)));
        assertEquals(1,service.search("Wi-Fi").size());
    }

    @ParameterizedTest
    @ValueSource(strings={"","   "})
    void blankSearchReturnsAll(String q){
        when(repo.findAll()).thenReturn(List.of(ticket(1L)));
        assertEquals(1,service.search(q).size());
        verify(repo,never()).search(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings={"","   ","not-an-email","a@","@example.com"})
    void rejectsInvalidEmail(String email){
        assertThrows(IllegalArgumentException.class,()->service.create("Student",email,"Software","Need help"));
        verify(repo,never()).save(any(),any(),any(),any(),any());
    }

    @Test void rejectsUnknownCategory(){
        assertThrows(IllegalArgumentException.class,()->service.create("Student","s@example.edu","Admin","Need help"));
        verify(repo,never()).save(any(),any(),any(),any(),any());
    }

    @Test void acceptsValidTicketAtBoundary(){
        String description="x".repeat(2000);
        when(repo.save(any(),any(),any(),eq(description),any())).thenReturn(ticket(4L));
        assertEquals(4L,service.create("Student","s@example.edu","Other",description).id());
    }

    @Test void rejectsDescriptionOverBoundary(){
        assertThrows(IllegalArgumentException.class,()->service.create("Student","s@example.edu","Other","x".repeat(2001)));
    }

    private Ticket ticket(long id){
        return new Ticket(id,"Morgan Diaz","mdiaz@example.edu","Network","Cannot connect to campus Wi-Fi",TicketStatus.IN_PROGRESS,"secret-token");
    }
}
