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

For each RoverPlan in the Mission:
1. Create a Rover instance using the plan's start Position
2. Execute the plan's instruction string character by character
3. Apply L/R/M with rotation and movement logic on the Rover instance

Policy behaviors:

STRICT: fail on first OOB move (throw OutOfBoundsException with rover index and step).

IGNORE: skip OOB M and continue.

STOP_ON_OOB: stop processing commands for that rover at first OOB.

The MissionRunner processes rover plans sequentially - each RoverPlan becomes a Rover instance that executes its instructions and returns its final Position.

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
