package edu.seminolestate.tickets.controller;
import edu.seminolestate.tickets.service.TicketService; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller
public class TicketController {
 private final TicketService service; public TicketController(TicketService service){this.service=service;}
 @GetMapping("/") public String home(){return "redirect:/tickets";}
 @GetMapping("/tickets") public String tickets(@RequestParam(required=false) String q,Model m){m.addAttribute("tickets",q==null?service.all():service.search(q));m.addAttribute("q",q);return "tickets";}
 @GetMapping("/tickets/new") public String form(){return "new-ticket";}
 @PostMapping("/tickets") public String create(@RequestParam String requesterName,@RequestParam String email,@RequestParam String category,@RequestParam String description){var t=service.create(requesterName,email,category,description);return "redirect:/tickets/"+t.id();}
 @GetMapping("/tickets/{id}") public String one(@PathVariable long id,Model m){m.addAttribute("ticket",service.get(id));return "ticket";}
 @PostMapping("/tickets/{id}/status") public String status(@PathVariable long id,@RequestParam String status){service.changeStatus(id,status);return "redirect:/tickets/"+id;}
}
