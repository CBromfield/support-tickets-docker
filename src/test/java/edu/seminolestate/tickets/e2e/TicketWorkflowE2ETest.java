package edu.seminolestate.tickets.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketWorkflowE2ETest {
    @Autowired MockMvc mvc;

    @Test void userCanCreateFindViewAndUpdateTicket() throws Exception {
        String location=mvc.perform(post("/tickets")
                .param("requesterName","Taylor Student")
                .param("email","taylor@example.edu")
                .param("category","Software")
                .param("description","Printer driver needs update"))
            .andExpect(status().is3xxRedirection())
            .andReturn().getResponse().getRedirectedUrl();

        mvc.perform(get("/tickets").param("q","Taylor Student"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Taylor Student")));

        mvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Printer driver needs update")));

        mvc.perform(post(location+"/status").param("status","CLOSED"))
            .andExpect(status().is3xxRedirection());

        mvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("CLOSED")));
    }

    @Test void invalidTicketSubmissionIsRejected() throws Exception {
        mvc.perform(post("/tickets")
                .param("requesterName","")
                .param("email","not-an-email")
                .param("category","Software")
                .param("description",""))
            .andExpect(status().isBadRequest());
    }

    @Test void missingTicketReturns404WithoutStackTrace() throws Exception {
        mvc.perform(get("/tickets/999999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Ticket not found"))
            .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Exception"))));
    }
}
