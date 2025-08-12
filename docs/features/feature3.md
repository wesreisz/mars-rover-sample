Story 3
Position Record

As a
domain developer modeling rover state

I want
an immutable Position record with (x,y,heading) and a move() utility class with static methods

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