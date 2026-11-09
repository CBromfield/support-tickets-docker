# Quality and Security Findings

## SQ-1 — SQL injection in search
**Original problem:** `TicketRepository.search` constructed SQL by concatenating the user's search term directly into the query. A value such as `' OR 1=1 --` could change the meaning of the SQL rather than being treated as search text.

**Significance:** This is an injection flaw (OWASP Top 10 2025 A05: Injection). It crosses the trust boundary between HTTP input and the database.

**Correction:** Search now uses JDBC bind parameters. `%`, `_`, and the escape character are escaped so wildcard characters supplied by the user are treated as literal search text. `TicketRepositorySecurityTest` verifies an SQL-injection payload does not become a match-all query.

## SQ-2 — Sensitive information in logs
**Original problem:** `TicketService.create` logged the entire `Ticket` record. The record contains the requester's email address and `accessToken`.

**Significance:** Logs often have different access and retention rules from application data. Writing credentials/tokens and personal data unnecessarily increases exposure if logs are accessed.

**Correction:** The service logs only the new ticket's id, category, and status. The access token and email are not logged.

## SQ-3 — Exception details exposed to the browser
**Original problem:** `TicketController` returned `ex` and `ex.getCause()` directly in an HTTP response.

**Significance:** Internal exception information can reveal implementation details and makes error behavior inconsistent. This aligns with OWASP Top 10 2025 A10: Mishandling of Exceptional Conditions.

**Correction:** `GlobalExceptionHandler` returns controlled 400, 404, and generic 500 responses. Unexpected exception details are logged on the server rather than returned to the client.

## SQ-4 — Missing input validation
**Original problem:** New-ticket fields were passed directly from the controller to persistence with no meaningful validation.

**Significance:** Invalid, blank, malformed, and excessively large input reduces data quality and can create reliability/security problems.

**Correction:** `TicketService` validates required fields, email format, allowed categories, search length, and field-length boundaries. Parameterized and boundary tests cover invalid and valid conditions.

## SQ-5 — Uncontrolled status parsing / missing-ticket update
**Original problem:** `TicketStatus.valueOf(status)` accepted unchecked request input and could throw an uncontrolled exception. The repository update did not establish that the requested ticket existed.

**Correction:** The service first verifies the ticket exists and then parses only a valid status, returning a controlled bad request for invalid status input.

## SQ-6 — Unnecessary H2 console exposure
**Original problem:** `spring.h2.console.enabled=true` enabled a development database interface that the application does not require.

**Significance:** Unneeded administrative interfaces increase attack surface and are a security-configuration concern.

**Correction:** The H2 console is disabled. Spring's error-message and stack-trace exposure are also explicitly disabled.

## SQ-7 — Limited test coverage
**Original problem:** The starter had only three service-level happy-path tests.

**Correction:** The revised suite adds invalid and boundary conditions, parameterized tests, repository-level injection tests, and an end-to-end HTTP workflow that creates, searches, views, and changes a ticket.
