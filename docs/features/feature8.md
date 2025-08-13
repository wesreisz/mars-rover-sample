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
