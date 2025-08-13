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