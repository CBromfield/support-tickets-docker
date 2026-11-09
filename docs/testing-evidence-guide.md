# Testing and Screenshot Guide

## Automated tests
Run `mvn test` (or Eclipse: Run As > Maven test). Capture the final summary showing all tests pass and `BUILD SUCCESS`. Mark the test count, failures/errors, and build result.

The suite covers: existing-ticket retrieval, missing tickets, valid status changes, multiple invalid statuses (parameterized), search behavior, blank search, multiple invalid emails (parameterized), invalid categories, description length boundary, SQL-injection/wildcard payloads, and end-to-end HTTP workflows.

## End-to-end test evidence
In the Maven output or JUnit view, identify `TicketWorkflowE2ETest`. It automates multiple HTTP/UI-level interactions against the full Spring application: create a ticket, search for it, view it, change status to CLOSED, and verify the changed status. It also verifies invalid submission and safe 404 behavior.

## Representative application functionality
Run `TicketApplication.java`, open `http://localhost:8080/tickets`, create a valid ticket, then open the created ticket. Capture a screenshot showing the created ticket and its fields/status. A second useful screenshot is the ticket after changing its status to CLOSED.

## Security verification
Run the full tests and use `TicketRepositorySecurityTest` as evidence that the SQL-injection payload is treated as data. `TicketWorkflowE2ETest.missingTicketReturns404WithoutStackTrace` demonstrates that missing resources return a controlled 404 without exception details.
