# Feature 9: Input Parsing & Validation - Implementation Plan

## Story Summary
Implement InputParser.parse(List<String> lines) → Mission, throws ParseException for parsing mission text into domain objects with clear error handling.

## Requirements Analysis

### Specific Requirements
- Implement `InputParser.parse(List<String> lines) → Mission, throws ParseException`
- Parse plateau definition: First non-empty line with `<maxX> <maxY>` integers ≥ 0
- Parse rover blocks: `<x> <y> <heading>` and `<instructions>` lines (no empties between)
- Validate heading ∈ {N,E,S,W} and instructions ∈ [LRM]+
- Validate plateau bounds and rover start positions are within bounds
- Provide clear error messages without NumberFormatException leaks
- Handle three specific scenarios with exact error message formats

### Integration Points
- Returns `Mission` record (existing domain object)
- Uses existing domain objects: `Plateau`, `RoverPlan`, `Position`, `Direction`
- Follows existing package structure: `com.example.mars.parse`
- Must create `ParseException` in `com.example.mars.domain` package per cursor rules

### Dependencies
- Java 21 with records and pattern matching
- JUnit 5 + AssertJ for testing
- Existing domain objects: Mission, Plateau, RoverPlan, Position, Direction
- Need to create ParseException class

## Codebase Analysis

### Existing Patterns
- **Records**: Immutable data carriers (Mission, Plateau, Position, RoverPlan)
- **Enums**: For closed sets (Direction with rotation methods)
- **Exception Handling**: IllegalArgumentException with specific messages
- **Testing**: Comprehensive unit tests with AssertJ, parameterized tests, acceptance criteria scenarios
- **Documentation**: Extensive Javadoc with @param, @return, @throws
- **Package Structure**: Clear separation by concern (domain, parse, exec, app)

### File Structure
```
src/main/java/com/example/mars/
├── domain/
│   └── ParseException.java (NEW)
└── parse/
    └── InputParser.java (NEW)

src/test/java/com/example/mars/
├── domain/
│   └── ParseExceptionTest.java (NEW)
└── parse/
    └── InputParserTest.java (NEW)
```

## Implementation Checklist

### Core Implementation
- [ ] 1. **Create ParseException class**
  - Create `/src/main/java/com/example/mars/domain/ParseException.java`
  - Extend `Exception` (checked exception)
  - Add constructors: `ParseException(String message)` and `ParseException(String message, Throwable cause)`
  - Add comprehensive Javadoc following existing patterns

- [ ] 2. **Create InputParser class**
  - Create `/src/main/java/com/example/mars/parse/InputParser.java`
  - Implement as utility class with static `parse(List<String> lines)` method
  - Add comprehensive Javadoc explaining the parsing contract
  - Import required domain classes

- [ ] 3. **Implement plateau parsing logic**
  - Filter non-empty lines from input
  - Parse first non-empty line as plateau definition
  - Split by whitespace and validate exactly 2 integers
  - Validate integers are ≥ 0
  - Create `Plateau` object
  - Throw `ParseException` with specific message format for invalid plateau lines

- [ ] 4. **Implement rover block parsing logic**
  - Parse remaining lines in pairs (position line + instructions line)
  - Validate position line format: `<x> <y> <heading>`
  - Validate heading is one of N, E, S, W
  - Validate instructions contain only L, R, M characters
  - Create `Position` and `RoverPlan` objects
  - Throw `ParseException` for invalid rover data

- [ ] 5. **Implement boundary validation**
  - Check each rover start position is within plateau bounds using `Plateau.contains()`
  - Throw `ParseException` with rover index and position details for out-of-bounds starts

- [ ] 6. **Implement error handling**
  - Catch `NumberFormatException` and wrap in `ParseException` with clear messages
  - Catch `IllegalArgumentException` from enum parsing and wrap appropriately
  - Ensure no implementation exceptions leak through

### Testing Implementation
- [ ] 7. **Create ParseExceptionTest**
  - Create `/src/test/java/com/example/mars/domain/ParseExceptionTest.java`
  - Test constructor with message
  - Test constructor with message and cause
  - Test inheritance from Exception
  - Follow existing test patterns (AssertJ assertions)

- [ ] 8. **Create InputParserTest**
  - Create `/src/test/java/com/example/mars/parse/InputParserTest.java`
  - Implement Scenario 1: Canonical input parsing
  - Implement Scenario 2: Bad plateau line error
  - Implement Scenario 3: Out-of-bounds start error
  - Add comprehensive edge case tests: empty input, malformed rover blocks, invalid instructions, invalid headings
  - Add boundary condition tests
  - Target 90%+ code coverage

### Acceptance Criteria Testing
- [ ] 9. **Test canonical input scenario**
  - Use standard Mars rover input: ["5 5", "1 2 N", "LMLMLMLMM", "3 3 E", "MMRMMRMRRM"]
  - Verify returned Mission has Plateau(5,5) and two correct RoverPlans
  - Verify rover positions and instruction strings match input

- [ ] 10. **Test error message formats**
  - Bad plateau: "Plateau line invalid (expected \"X Y\"): \"5 X\""
  - OOB start: "Rover #1 start out of bounds: (7,1) > plateau (5,5)"
  - Verify exact message formats match acceptance criteria

### Comprehensive Validation
- [ ] 11. **Add comprehensive validation tests**
  - Test empty input list
  - Test all whitespace input
  - Test missing rover instruction lines
  - Test negative plateau coordinates
  - Test invalid heading characters (not N,E,S,W)
  - Test invalid instruction characters (not L,R,M)
  - Test rover numbering in error messages (1-indexed)

- [ ] 12. **Verify integration compatibility**
  - Ensure returned Mission objects are compatible with existing domain usage
  - Verify exception handling follows existing patterns
  - Test with edge cases that might be used by future MissionRunner

## Acceptance Criteria Validation

### Scenario 1 – Canonical Input
**Given** the canonical test input  
**When** I parse it  
**Then** I get Plateau(5,5) and two RoverPlans matching the input

### Scenario 2 – Bad Plateau Line
**Given** the plateau line "5 X"  
**When** I parse  
**Then** a ParseException is thrown  
**And** the message contains "Plateau line invalid (expected \"X Y\"): \"5 X\""

### Scenario 3 – OOB Start
**Given** plateau "5 5" and rover start "7 1 N"  
**When** I parse  
**Then** a ParseException is thrown  
**And** the message contains "Rover #1 start out of bounds: (7,1) > plateau (5,5)"

## Definition of Done

- [ ] Unit tests cover valid/invalid plateau, heading, instructions, OOB starts
- [ ] Clear error messages; no NumberFormatException leaks
- [ ] 90%+ coverage for parser
- [ ] All acceptance criteria scenarios pass
- [ ] Integration with existing domain objects verified
- [ ] Follows existing code conventions and patterns
