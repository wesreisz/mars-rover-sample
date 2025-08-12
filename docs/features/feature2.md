Rover Direction System

As a
domain developer implementing rover navigation

I want
a Direction system that supports rotation and movement deltas

So that
rover motion can be computed deterministically

Description
Implement Direction {N,E,S,W} with:

rotateLeft(), rotateRight()

dx() and dy() to indicate movement deltas for a forward step

Acceptance Criteria ✅
Scenario 1 – Left Rotations
Given a direction N
When I call rotateLeft() repeatedly
Then I get W, then S, then E, then N

Scenario 2 – Right Rotations
Given a direction N
When I call rotateRight() repeatedly
Then I get E, then S, then W, then N

Scenario 3 – Movement Deltas
Given direction E
When I query deltas
Then dx() = 1 and dy() = 0 (and analogously for N, S, W)

Definition of Done (DoD)

Table-driven unit tests cover all directions for left/right & deltas.

No IO; pure functions.

90%+ coverage for this unit.