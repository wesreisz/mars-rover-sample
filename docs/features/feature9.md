Story 9
Input Parsing & Validation

As a
mission operator providing input via STDIN

I want
the system to parse mission text into domain objects with clear errors

So that
invalid inputs are rejected deterministically

Description
Implement InputParser.parse(List<String> lines) → Mission, throws ParseException.
Rules:

First non-empty line: <maxX> <maxY> integers, ≥ 0.

Then repeated rover blocks: <x> <y> <heading> and <instructions> (no empties).

Heading ∈ {N,E,S,W}; instructions ∈ [LRM]+.

Validate plateau bounds and rover start in bounds.

Acceptance Criteria ✅
Scenario 1 – Canonical Input
Given the canonical test input
When I parse it
Then I get Plateau(5,5) and two RoverPlans matching the input

Scenario 2 – Bad Plateau Line
Given the plateau line "5 X"
When I parse
Then a ParseException is thrown
And the message contains Plateau line invalid (expected "X Y"): "5 X"

Scenario 3 – OOB Start
Given plateau "5 5" and rover start "7 1 N"
When I parse
Then a ParseException is thrown
And the message contains Rover #1 start out of bounds: (7,1) > plateau (5,5)

Definition of Done (DoD)

Unit tests cover valid/invalid plateau, heading, instructions, OOB starts.

Clear error messages; no NumberFormatException leaks.

90%+ coverage for parser.
