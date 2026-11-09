# Selected Correction Evidence

## 1. SQL search: concatenation -> parameterized query
**Before:** `TicketRepository.search` inserted `term` directly into the SQL string.

**After:** the repository uses `?` placeholders and passes the search pattern separately to `JdbcTemplate.query`. LIKE metacharacters are escaped.

**Why:** The database now receives the search value as data rather than executable SQL syntax, addressing the injection vulnerability at its source.

## 2. Logging: full Ticket -> safe metadata
**Before:** `log.info("Created support ticket: {}", t)` serialized the record, including email and access token, into logs.

**After:** `log.info("Created support ticket id={} category={} status={}", ...)` logs only non-sensitive operational metadata.

**Why:** This removes unnecessary sensitive data from logs while preserving useful operational information.

## 3. Error handling: raw exception -> controlled responses
**Before:** the controller returned `"Application error: "+ex+"\nCause: "+ex.getCause()` to the browser.

**After:** `GlobalExceptionHandler` maps invalid input to 400, missing tickets to 404, and unexpected failures to a generic 500 message while logging details server-side.

**Why:** Client behavior becomes predictable and internal implementation details are no longer disclosed.

## 4. Validation: unchecked input -> service boundary validation
**Before:** create/status values flowed from HTTP request parameters to application logic/persistence without meaningful checks.

**After:** `TicketService` enforces required values, email format, allowed categories, length boundaries, valid status values, and ticket existence.

**Why:** The service layer now protects its own invariants regardless of which controller or caller invokes it.

## 5. Testing: three happy paths -> layered security/quality coverage
**Before:** three service tests covered existing-ticket retrieval, one valid status change, and one search.

**After:** tests include parameterized invalid inputs, boundaries, missing records, SQL-injection payloads, wildcard handling, and a multi-step end-to-end HTTP workflow.

**Why:** The corrections now have regression tests, and important failure/security conditions are exercised in addition to successful behavior.
