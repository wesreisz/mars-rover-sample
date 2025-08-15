Story 12
Acceptance & Property Tests

As a
developer ensuring system quality

I want
comprehensive acceptance tests and optional property tests

So that
we meet our coverage and reliability goals

Description
Add parameterized acceptance tests:

Canonical example → exact output lines.

Rotate-only paths (LLLL, RRRR).

Edge boundaries at (0,0) and (maxX,maxY).

OOB scenarios for each policy (STRICT, IGNORE, STOP_ON_OOB).
Optional property test: random instruction strings under STRICT should fail on OOB with clear exceptions.

Acceptance Criteria ✅
Scenario 1 – Canonical Acceptance
Given the canonical input
When tests run
Then expected output lines are matched exactly

Scenario 2 – Coverage Gate
Given the test suite
When I run coverage
Then domain and parser packages report ≥ 90% coverage

Definition of Done (DoD)

./gradlew spotlessCheck check passes.

Coverage ≥ 90% (domain + parser).

No flaky tests; deterministic assertions.