# Feature 5 Implementation Checklist - Rover Class

## Implementation Checklist

### 1. ~~Create Position record~~ ✅ ALREADY EXISTS
- [x] File: `src/main/java/com/example/mars/domain/Position.java` ✅ EXISTS
- [x] Record with fields: int x, int y, Direction heading ✅ IMPLEMENTED
- [x] Add constructor validation if needed ✅ N/A (record handles this)
- [x] Add Javadoc documentation ✅ DONE

### 2. Create Rover class
- [ ] File: `src/main/java/com/example/mars/domain/Rover.java`
- [ ] Private Position field
- [ ] Constructor taking initial Position
- [ ] rotateLeft() method updating heading via Direction.rotateLeft()
- [ ] rotateRight() method updating heading via Direction.rotateRight()
- [ ] peekMove() method calculating next position without mutation
- [ ] move() method updating position using peekMove() result
- [ ] getPosition() getter method
- [ ] Add comprehensive Javadoc

### 3. ~~Create Position unit tests~~ ✅ ALREADY EXISTS
- [x] File: `src/test/java/com/example/mars/domain/PositionTest.java` ✅ EXISTS
- [x] Test record creation with valid inputs ✅ IMPLEMENTED
- [x] Test immutability properties ✅ VERIFIED IN TESTS
- [x] Test edge cases (negative coordinates, all directions) ✅ COMPREHENSIVE COVERAGE

### 4. Create Rover unit tests
- [ ] File: `src/test/java/com/example/mars/domain/RoverTest.java`
- [ ] Table-driven tests for rotateLeft() across all headings
- [ ] Table-driven tests for rotateRight() across all headings
- [ ] Test peekMove() doesn't mutate rover state (all directions)
- [ ] Test move() updates rover state correctly (all directions)
- [ ] Test acceptance criteria scenarios explicitly
- [ ] Verify 90%+ coverage requirement

### 5. Run quality checks
- [ ] Execute `./gradlew spotlessApply` to format code
- [ ] Execute `./gradlew spotlessCheck check` to verify standards
- [ ] Execute `./gradlew test jacocoTestReport` to verify coverage
- [ ] Fix any formatting or quality issues

### 6. Verification
- [ ] Confirm all acceptance criteria scenarios pass
- [ ] Verify 90%+ coverage for Position and Rover classes
- [ ] Ensure no regressions in existing Direction tests
- [ ] Validate integration between Position, Direction, and Rover

## Acceptance Criteria Verification

### Scenario 1 – Peek Does Not Mutate
- [ ] Given a rover at (1,1,N)
- [ ] When I call peekMove()
- [ ] Then I get (1,2,N)
- [ ] And the rover's internal position remains (1,1,N)

### Scenario 2 – Move Updates State
- [ ] Given a rover at (1,1,N)
- [ ] When I call move()
- [ ] Then the rover's position becomes (1,2,N)

## Definition of Done
- [ ] Unit tests cover rotations, peek, move across headings
- [ ] No IO; pure domain logic
- [ ] 90%+ coverage for this unit
