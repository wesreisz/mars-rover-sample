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