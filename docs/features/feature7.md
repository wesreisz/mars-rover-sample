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