# Dependency Security Review

The project uses Maven and inherits dependency versions from Spring Boot 3.5.6. An OWASP Dependency-Check Maven plugin has been added to `pom.xml` so the dependency tree can be analyzed for known published vulnerabilities.

## Run the analysis
From the project root run:

`mvn org.owasp:dependency-check-maven:check`

The generated HTML report is normally placed under `target/dependency-check-report.html`. Because `target/` is generated build output, use the report for evidence/screenshots but do not include `target/` in the submitted source ZIP.

## What to document
Record the date of the scan, the number of dependencies analyzed, and any actionable vulnerabilities reported. If the scan reports an actionable vulnerable dependency, update the appropriate direct dependency or Spring Boot parent version after checking compatibility, rerun tests, and rerun the scan. If it reports no actionable vulnerabilities, state that result and include a screenshot of the report rather than inventing a vulnerability.

**Important:** The final scan result must come from the scan executed on the student's workstation. It is not pre-filled here because vulnerability databases change over time and this environment did not have Maven available to execute the scanner.
