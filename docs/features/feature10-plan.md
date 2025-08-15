# Feature 10: Mission Execution (MissionRunner) - Implementation Plan

## Story Summary
Implement MissionRunner.run(Mission, BoundaryPolicy) → List<Position> for executing rover plans sequentially with policy-aware out-of-bounds handling.

## 1. Requirements Analysis

### Specific Requirements
- Implement `MissionRunner.run(Mission, BoundaryPolicy) → List<Position>`
- Process rover plans sequentially from the Mission
- For each RoverPlan: create Rover instance using plan's start Position
- Execute plan's instruction string character by character
- Apply L/R/M with rotation and movement logic on Rover instance
- Handle three BoundaryPolicy behaviors:
  - **STRICT**: Throw OutOfBoundsException on first OOB move with rover index, step, and from-position
  - **IGNORE**: Skip OOB M commands and continue processing
  - **STOP_ON_OOB**: Stop processing commands for that rover at first OOB
- Return final positions deterministically
- No IO in runner (deterministic logic only)
- 90%+ test coverage requirement

### Integration Points
- Uses existing `Mission` record (plateau + List<RoverPlan>)
- Uses existing `BoundaryPolicy` enum with STRICT/IGNORE/STOP_ON_OOB values
- Uses existing `Rover` class with movement/rotation methods
- Uses existing `Position`, `Direction`, `Plateau` domain objects
- Follows existing package structure: `com.example.mars.exec`
- Must create new `OutOfBoundsException` in `com.example.mars.domain`

### Dependencies
- All existing domain objects: Mission, Rover, Position, Direction, Plateau, RoverPlan
- BoundaryPolicy enum (existing in exec package)
- OutOfBoundsException class (NEW - needs creation)
- Java 17 features, JUnit 5 + AssertJ for testing

## 2. Codebase Analysis

### Existing Patterns
- **Records**: Immutable data carriers (Mission, Position, Plateau, RoverPlan)
- **Enums**: Closed sets with methods (Direction with rotation, BoundaryPolicy)
- **Exception Handling**: Custom exceptions with descriptive messages (ParseException pattern)
- **Testing**: Comprehensive unit tests with acceptance criteria scenarios, parameterized tests, AssertJ assertions
- **Documentation**: Extensive Javadoc with @param, @return, @throws annotations
- **Package Structure**: Clear separation - domain, exec, parse, app
- **Static Utility Classes**: No instances needed for processing logic

### Integration Points
- `Mission.plateau()` and `Mission.plans()` for data access
- `Rover` constructor and movement methods: `rotateLeft()`, `rotateRight()`, `move()`, `peekMove()`, `getPosition()`
- `Plateau.contains(x, y)` for boundary validation
- `BoundaryPolicy` enum values for behavior control

### Reusable Components
- All existing domain objects and their methods
- Test patterns from `RoverTest`, `MissionTest`, `InputParserTest`
- Exception patterns from `ParseException`
- Static utility approach from existing codebase patterns

## 3. Implementation Plan

### Component/File Structure
```
src/main/java/com/example/mars/domain/OutOfBoundsException.java (NEW)
src/main/java/com/example/mars/exec/MissionRunner.java (NEW)
src/test/java/com/example/mars/exec/MissionRunnerTest.java (NEW)
```

### Required Changes to Existing Code
- No changes to existing code required
- Pure addition of new components following existing patterns

### New Components/Modules

#### OutOfBoundsException.java
- Custom exception extending Exception (following ParseException pattern)
- Constructor with detailed message including rover index, instruction index, and from-position
- Package: `com.example.mars.domain`
- Comprehensive Javadoc documentation
- Follows existing exception patterns for message format

#### MissionRunner.java
- Static utility class (no instance state needed)
- Public static method: `run(Mission mission, BoundaryPolicy policy) throws OutOfBoundsException`
- Sequential processing of rover plans with index tracking
- Character-by-character instruction processing loop
- Policy-aware boundary handling with switch statement
- Package: `com.example.mars.exec`
- Comprehensive Javadoc with examples

#### MissionRunnerTest.java
- Unit test class following existing test patterns
- Acceptance criteria scenarios from feature story
- All three policy behaviors tested comprehensively
- Edge cases: empty missions, invalid instructions, boundary conditions
- Exception message validation
- 90%+ coverage targeting
- Uses AssertJ assertions and parameterized tests

## 4. Implementation Details

### OutOfBoundsException Structure
```java
package com.example.mars.domain;

/**
 * Exception thrown when a rover attempts to move outside the defined plateau boundaries
 * during mission execution. Provides detailed context about the violation including
 * rover identification and position information.
 */
public class OutOfBoundsException extends Exception {
  public OutOfBoundsException(String message) {
    super(message);
  }
}
```

### MissionRunner Structure
```java
package com.example.mars.exec;

import com.example.mars.domain.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Executes rover missions with configurable boundary policies.
 */
public class MissionRunner {
  
  /**
   * Executes a mission by processing rover plans sequentially.
   *
   * @param mission the mission containing plateau and rover plans
   * @param policy the boundary policy for out-of-bounds handling
   * @return list of final rover positions in execution order
   * @throws OutOfBoundsException if STRICT policy and OOB move attempted
   */
  public static List<Position> run(Mission mission, BoundaryPolicy policy) 
      throws OutOfBoundsException {
    // Implementation with sequential processing
  }
}
```

### Test Structure
- Acceptance criteria scenario tests
- Policy-specific behavior tests
- Edge case handling
- Exception message validation
- Coverage targeting 90%+

## 5. Implementation Checklist

### ✅ IMPLEMENTATION CHECKLIST:

1. Create `OutOfBoundsException.java` in `src/main/java/com/example/mars/domain/` directory
2. Add constructor with String message parameter
3. Add comprehensive Javadoc documentation for OutOfBoundsException
4. Create `MissionRunner.java` in `src/main/java/com/example/mars/exec/` directory
5. Add package declaration and required imports (Mission, BoundaryPolicy, Position, etc.)
6. Implement static `run(Mission, BoundaryPolicy)` method signature with throws clause
7. Add List<Position> return type and ArrayList initialization
8. Implement sequential rover plan processing loop with index tracking
9. Create Rover instance for each plan using plan.start() position
10. Implement character-by-character instruction processing loop
11. Add instruction character validation (L, R, M only)
12. Implement L command handling with rover.rotateLeft()
13. Implement R command handling with rover.rotateRight()
14. Implement M command with boundary checking using rover.peekMove()
15. Add plateau.contains() boundary validation logic
16. Implement STRICT policy: throw OutOfBoundsException with rover index, instruction index, from-position
17. Implement IGNORE policy: skip OOB move, continue with next instruction
18. Implement STOP_ON_OOB policy: break from instruction loop, preserve current position
19. Add rover.getPosition() to final results list after plan completion
20. Add comprehensive Javadoc documentation for MissionRunner class and run method
21. Create `MissionRunnerTest.java` in `src/test/java/com/example/mars/exec/` directory
22. Add test class declaration with proper imports (JUnit 5, AssertJ, domain objects)
23. Implement acceptance criteria scenario 1: canonical example with STRICT policy
24. Implement acceptance criteria scenario 2: OOB in STRICT with exception validation
25. Implement acceptance criteria scenario 3: OOB in IGNORE with completion verification
26. Implement acceptance criteria scenario 4: OOB in STOP_ON_OOB with early termination
27. Add test for empty mission (no rover plans)
28. Add test for mission with single rover plan
29. Add test for multiple rover plans in sequence
30. Add test for invalid instruction characters
31. Add parameterized tests for all policy combinations
32. Add edge case tests for boundary conditions
33. Add exception message format validation tests
34. Verify 90%+ test coverage requirement
35. Run all tests to ensure functionality
36. Verify integration with existing codebase (no breaking changes)

## 6. Verification Checklist

### Requirements Coverage
- ✅ MissionRunner.run(Mission, BoundaryPolicy) → List<Position> signature implemented
- ✅ Sequential rover plan processing with deterministic results
- ✅ Character-by-character instruction execution (L/R/M)
- ✅ STRICT policy: OutOfBoundsException with rover index, instruction index, from-position
- ✅ IGNORE policy: skip OOB moves, continue processing
- ✅ STOP_ON_OOB policy: halt rover processing at first OOB
- ✅ Deterministic logic with no IO operations
- ✅ 90%+ test coverage achieved
- ✅ All acceptance criteria scenarios pass

### Integration Verification
- ✅ Uses existing Mission, BoundaryPolicy, Rover, Position domain objects
- ✅ Follows existing package structure and naming conventions
- ✅ Exception handling follows ParseException patterns
- ✅ Test structure follows existing test patterns
- ✅ No modifications to existing code required

### Assumptions and Decisions
- OutOfBoundsException message format: "Rover #<index> instruction <index> out of bounds from (<x>,<y>,<heading>)"
- Rover index is 1-based for user-friendly error messages
- Instruction index is 1-based for user-friendly error messages
- Invalid instruction characters cause runtime exceptions (fail-fast approach)
- STOP_ON_OOB affects only the current rover, not subsequent rovers in mission
- Empty missions return empty position lists (valid scenario)
