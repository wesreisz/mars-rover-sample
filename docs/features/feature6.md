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