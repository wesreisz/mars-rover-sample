Story 1
Project Bootstrap

As a
developer starting the Mars Rovers console app

I want
a clean Java 17 Gradle project with quality gates and packaging set up

So that
we can iterate quickly with consistent formatting, tests, and a runnable fat JAR

Description
Initialize a Gradle (Kotlin DSL) Java 17 project with the required packages and tooling:

Packages: com.example.mars.app, com.example.mars.domain, com.example.mars.parse, com.example.mars.exec.

Plugins: Spotless (Google Java Format), Checkstyle (minimal rules), Shadow (fat JAR).

Dependencies: no production deps; test-only JUnit 5, AssertJ, Mockito (test scope).

Main.java prints a placeholder line to STDOUT.

README with build/run instructions.

Acceptance Criteria ✅
Scenario 1 – Build & Quality Gates
Given the repo is freshly cloned
When I run ./gradlew spotlessCheck check
Then the build succeeds with no violations

Scenario 2 – Runnable Fat JAR
Given the project is built
When I run ./gradlew shadowJar
Then build/libs/mars-rovers-all.jar is created
And java -jar build/libs/mars-rovers-all.jar prints "Mars Rovers App Initialized"

Definition of Done (DoD)

Gradle config targets Java 17; shadow JAR includes Main-Class.

Spotless/Checkstyle configured; both pass.

README explains build and run.

Story 2
Heading Enum

As a
domain developer implementing rover navigation

I want
a Heading enum that supports rotation and movement deltas

So that
rover motion can be computed deterministically

Description
Implement Heading {N,E,S,W} with:

rotateLeft(), rotateRight()

dx() and dy() to indicate movement deltas for a forward step

Acceptance Criteria ✅
Scenario 1 – Left Rotations
Given a heading N
When I call rotateLeft() repeatedly
Then I get W, then S, then E, then N

Scenario 2 – Right Rotations
Given a heading N
When I call rotateRight() repeatedly
Then I get E, then S, then W, then N

Scenario 3 – Movement Deltas
Given heading E
When I query deltas
Then dx() = 1 and dy() = 0 (and analogously for N, S, W)

Definition of Done (DoD)

Table-driven unit tests cover all headings for left/right & deltas.

No IO; pure functions.

90%+ coverage for this unit.

Story 3
Position Record

As a
domain developer modeling rover state

I want
an immutable Position record with (x,y,heading) and a move() helper

So that
state transitions are clean and side-effect free

Description
Create record Position(int x, int y, Heading heading) with:

Position move(Heading h) → new Position advanced by h.dx()/h.dy(), preserving heading

Acceptance Criteria ✅
Scenario 1 – Move North
Given position (2,3,N)
When I call move(N)
Then I get a new Position (2,4,N)
And the original is unchanged

Scenario 2 – Move West
Given position (5,5,W)
When I call move(W)
Then I get (4,5,W)

Definition of Done (DoD)

Unit tests cover moves from various coordinates across all headings.

Immutability preserved.

90%+ coverage for this unit.

Story 4
Plateau Record - Size of the operating area for the Rover(s)

As a
mission operator defining the operational area

I want
a Plateau record with inclusive boundaries and a contains(x,y) check

So that
rover moves can be validated against the plateau

Description
Create record Plateau(int maxX, int maxY) with:

boolean contains(int x, int y) where 0 ≤ x ≤ maxX and 0 ≤ y ≤ maxY

Acceptance Criteria ✅
Scenario 1 – Inside Bounds
Given a plateau (5,5)
When I check (0,0), (5,5), (3,2)
Then all return true

Scenario 2 – Outside Bounds
Given a plateau (5,5)
When I check (-1,0), (6,0), (0,6)
Then all return false

Definition of Done (DoD)

Unit tests cover inside, edge, outside, and negative coordinates.

90%+ coverage for this unit.

Story 5
Rover Class

As a
domain developer implementing rover behavior

I want
a Rover that can rotate, peek a move, and move

So that
we can execute instructions deterministically

Description
Implement Rover holding a Position with methods:

rotateLeft(), rotateRight() (updates heading)

peekMove() → calculated next Position without mutating

move() → update to the next Position

Acceptance Criteria ✅
Scenario 1 – Peek Does Not Mutate
Given a rover at (1,1,N)
When I call peekMove()
Then I get (1,2,N)
And the rover’s internal position remains (1,1,N)

Scenario 2 – Move Updates State
Given a rover at (1,1,N)
When I call move()
Then the rover’s position becomes (1,2,N)

Definition of Done (DoD)

Unit tests cover rotations, peek, move across headings.

No IO; pure domain logic.

90%+ coverage for this unit.

Story 6
BoundaryPolicy Enum

As a
mission operator configuring safety behavior

I want
to specify how out-of-bounds (OOB) moves are handled

So that
missions can run in strict, ignore, or stop-on-oob modes

Description
Create BoundaryPolicy enum in exec with:

STRICT, IGNORE, STOP_ON_OOB (documentation only—logic lives in MissionRunner)

Acceptance Criteria ✅
Scenario 1 – Policies Exist
Given the codebase
When I reference BoundaryPolicy
Then I can select STRICT, IGNORE, STOP_ON_OOB
And each has Javadoc describing its behavior

Definition of Done (DoD)

Enum implemented with Javadoc.

Compiles and used in MissionRunner later.

Story 7
RoverPlan Record

As a
mission operator defining a single rover’s plan

I want
to store a start position and its instruction string immutably

So that
execution can process plans sequentially

Description
Create record RoverPlan(Position start, String instructions).
Validation of instructions is handled by parser; this is a data holder.

Acceptance Criteria ✅
Scenario 1 – Construct and Read
Given a Position and an instruction string
When I create a RoverPlan
Then getters return the provided values

Definition of Done (DoD)

Record compiles; simple unit test for construction.

No validation here (parser will enforce).

Story 8
Mission Record

As a
mission operator bundling a full mission

I want
to store the plateau and ordered rover plans

So that
the runner can execute them sequentially

Description
Create record Mission(Plateau plateau, List<RoverPlan> plans).

Acceptance Criteria ✅
Scenario 1 – Construct and Read
Given a plateau and a list of RoverPlans
When I create a Mission
Then getters return the values as provided
And the order of plans is preserved

Definition of Done (DoD)

Record compiles; simple unit test for construction & order.

No IO.

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

Story 10
Mission Execution (MissionRunner)

As a
mission operator running rovers

I want
the system to execute rover plans sequentially with policy-aware OOB handling

So that
final positions are produced deterministically

Description
Implement MissionRunner.run(Mission, BoundaryPolicy) → List<Position>.

Apply L/R/M with rotation and movement logic.

Policy behaviors:

STRICT: fail on first OOB move (throw OutOfBoundsException with rover index and step).

IGNORE: skip OOB M and continue.

STOP_ON_OOB: stop processing commands for that rover at first OOB.

Acceptance Criteria ✅
Scenario 1 – Canonical Example
Given plateau 5 5 and the two canonical rover plans
When I run with STRICT
Then the final outputs are 1 3 N and 5 1 E

Scenario 2 – OOB in STRICT
Given plateau 5 5 and rover at 0 0 S with "M"
When I run with STRICT
Then an OutOfBoundsException is thrown
And its message references rover #1, instruction index 1, and from-position (0,0,S)

Scenario 3 – OOB in IGNORE
Given the same setup
When I run with IGNORE
Then execution completes
And the rover remains at (0,0,S)

Scenario 4 – OOB in STOP_ON_OOB
Given the same setup
When I run with STOP_ON_OOB
Then execution stops for that rover after the first step
And the rover remains at (0,0,S)

Definition of Done (DoD)

Unit tests for all policies and mixed instruction sequences.

Deterministic logic; no IO in runner.

90%+ coverage for runner.

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