# Student Support Ticket System — Revised

Revised project for the Software Quality & Security assignment.

## Requirements
- Java 21 or later
- Maven 3.9+

## Run
```bash
mvn spring-boot:run
```
Then open `http://localhost:8080/tickets`.

## Tests
```bash
mvn test
```

## Dependency security scan
```bash
mvn org.owasp:dependency-check-maven:check
```
Review `target/dependency-check-report.html`. The `target` directory is generated output and should not be submitted in the project ZIP.

## Main improvements
- Parameterized ticket search prevents SQL injection.
- Sensitive email/access-token values are no longer logged.
- Controlled 400/404/500 error handling prevents exception-detail disclosure.
- Ticket creation, search, and status changes validate untrusted input.
- The unnecessary H2 web console is disabled.
- Automated coverage includes parameterized, boundary, security, repository, and end-to-end HTTP workflow tests.

See `docs/` for findings, issue records, correction evidence, dependency-review instructions, and screenshot guidance.
