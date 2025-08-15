Story 11
CLI & Error Mapping

As a
developer delivering a console UX

I want
a CLI that reads from STDIN, supports flags, prints results, and maps errors to exit codes

So that
the app behaves predictably in pipelines and local runs

Description
Implement Main to:

Parse flags: --strict (default), --ignore-oob, --stop-on-oob, --fail-fast (default), --collect-errors.

Read all input from STDIN.

Use InputParser and MissionRunner.

Print final positions to STDOUT (one per line, in order).

Print errors to STDERR with rover index, line numbers (if available), and failing instruction index.

Exit codes: 0 success; 1 validation/execution error; 2 usage error.

Acceptance Criteria ✅
Scenario 1 – Canonical Run
Given the canonical test input via STDIN
When I run java -jar mars-rovers.jar
Then STDOUT contains:

mathematica
Copy
Edit
1 3 N
5 1 E
And the process exits with code 0

Scenario 2 – Usage Error
Given an unknown flag --wat
When I run the program
Then STDERR contains a usage message
And the process exits with code 2

Scenario 3 – Validation Error (STRICT)
Given plateau "5 X" in input
When I run with default flags
Then STDERR contains Plateau line invalid (expected "X Y"): "5 X"
And the process exits with code 1

Definition of Done (DoD)

Deterministic STDERR messages and exit codes.

No logging libraries; pure console IO.

Verified with integration tests.