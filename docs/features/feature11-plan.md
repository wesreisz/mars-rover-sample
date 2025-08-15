# Story 11 Implementation Plan
## CLI & Error Mapping

### 1. Requirements Analysis

#### Specific Requirements from Story 11:
- **CLI Flag Parsing**: Support flags `--strict` (default), `--ignore-oob`, `--stop-on-oob`, `--fail-fast` (default), `--collect-errors`
- **STDIN Input Processing**: Read all mission input from STDIN
- **Component Integration**: Use existing `InputParser` and `MissionRunner` 
- **Output Formatting**: Print final rover positions to STDOUT (one per line, in order)
- **Error Handling**: Print errors to STDERR with rover index, line numbers, and failing instruction index
- **Exit Code Mapping**: 0 (success), 1 (validation/execution error), 2 (usage error)

#### Integration with Existing Functionality:
- Leverages existing `InputParser.parse()` for mission parsing
- Uses `MissionRunner.run()` with appropriate `BoundaryPolicy`
- Integrates with existing exception types (`ParseException`, `OutOfBoundsException`)
- Maintains existing domain models (`Mission`, `Position`, etc.)

#### Dependencies and Prerequisites:
- All core domain logic implemented (Stories 1-10) ✅
- `InputParser` with `ParseException` handling ✅
- `MissionRunner` with `BoundaryPolicy` support ✅
- `OutOfBoundsException` for execution errors ✅

### 2. Codebase Analysis

#### Existing Patterns and Conventions:
- **Package Structure**: `com.example.mars.app` for application layer
- **Error Handling**: Custom exceptions with descriptive messages
- **Static Utilities**: `InputParser.parse()`, `MissionRunner.run()` pattern
- **Immutable Records**: Domain objects as immutable records/classes
- **Javadoc**: Comprehensive documentation with usage examples

#### Integration Points:
- **Main.java**: Currently placeholder, needs complete CLI implementation
- **InputParser**: Already handles STDIN-like input via `List<String>`
- **MissionRunner**: Supports all required boundary policies
- **Exception Types**: `ParseException` and `OutOfBoundsException` provide needed error context

#### Reusable Components:
- `InputParser.parse(List<String>)` - converts lines to `Mission`
- `MissionRunner.run(Mission, BoundaryPolicy)` - executes mission
- `BoundaryPolicy` enum - maps to CLI flags
- Exception messages - already include rover indices and context

### 3. Implementation Plan

#### Component/File Structure:
```
src/main/java/com/example/mars/app/
├── Main.java (complete rewrite)
└── CliArguments.java (new - argument parsing and validation)

src/test/java/com/example/mars/app/
├── MainTest.java (new - integration tests)
└── CliArgumentsTest.java (new - unit tests)
```

#### Required Changes to Existing Code:
- **Main.java**: Complete rewrite from placeholder to full CLI implementation
- **No changes to existing domain/parse/exec packages** - they're complete

#### New Components to be Created:
1. **CliArguments.java** - Encapsulates CLI argument parsing and validation
2. **MainTest.java** - Integration tests covering all scenarios
3. **CliArgumentsTest.java** - Unit tests for argument parsing

### 4. Implementation Details

#### Scenario-by-Scenario Implementation:

##### **Scenario 1: Canonical Run**
**Input**: Canonical test input via STDIN
**Expected**: Final positions to STDOUT, exit code 0

**Implementation Steps**:
1. Parse CLI arguments (defaults: `--strict`, `--fail-fast`)
2. Read all lines from `System.in`
3. Call `InputParser.parse(lines)` → `Mission`
4. Call `MissionRunner.run(mission, STRICT)` → `List<Position>`
5. Print each position as "x y heading" to `System.out`
6. Exit with code 0

**Code Structure**:
```java
public static void main(String[] args) {
    try {
        CliArguments cli = CliArguments.parse(args);
        List<String> inputLines = readStdin();
        Mission mission = InputParser.parse(inputLines);
        List<Position> finalPositions = MissionRunner.run(mission, cli.getBoundaryPolicy());
        
        for (Position pos : finalPositions) {
            System.out.println(pos.x() + " " + pos.y() + " " + pos.heading());
        }
        System.exit(0);
    } catch (CliArgumentsException e) {
        // Handle usage errors (Scenario 2)
    } catch (ParseException | OutOfBoundsException e) {
        // Handle validation/execution errors (Scenario 3)
    }
}
```

##### **Scenario 2: Usage Error**
**Input**: Unknown flag `--wat`
**Expected**: Usage message to STDERR, exit code 2

**Implementation Steps**:
1. `CliArguments.parse(args)` detects unknown flag
2. Throw `CliArgumentsException` with usage message
3. Catch in main, print to `System.err`
4. Exit with code 2

**Error Message Format**:
```
Usage: java -jar mars-rovers.jar [OPTIONS]
Options:
  --strict      Fail on out-of-bounds moves (default)
  --ignore-oob  Skip out-of-bounds moves
  --stop-on-oob Stop rover on out-of-bounds
  --fail-fast   Stop on first error (default)
  --collect-errors Continue processing after errors

Unknown option: --wat
```

##### **Scenario 3: Validation Error (STRICT)**
**Input**: Plateau "5 X" in input
**Expected**: Parse error to STDERR, exit code 1

**Implementation Steps**:
1. Read STDIN successfully
2. `InputParser.parse()` throws `ParseException` for invalid plateau
3. Catch `ParseException` in main
4. Print error message to `System.err`
5. Exit with code 1

**Error Message Format**:
```
Parse Error: Plateau line invalid (expected "X Y"): "5 X"
```

#### Flag-to-Policy Mapping:
```java
private BoundaryPolicy getBoundaryPolicy() {
    if (ignoreOob) return BoundaryPolicy.IGNORE;
    if (stopOnOob) return BoundaryPolicy.STOP_ON_OOB;
    return BoundaryPolicy.STRICT; // default
}
```

#### Error Handling Strategy:
- **CliArgumentsException** → Usage errors (exit 2)
- **ParseException** → Validation errors (exit 1)  
- **OutOfBoundsException** → Execution errors (exit 1)
- **IOException** → Input reading errors (exit 1)

### 5. Implementation Checklist

#### Core CLI Requirements:
- [ ] Parse `--strict` flag (default behavior)
- [ ] Parse `--ignore-oob` flag → `BoundaryPolicy.IGNORE`
- [ ] Parse `--stop-on-oob` flag → `BoundaryPolicy.STOP_ON_OOB`
- [ ] Parse `--fail-fast` flag (default behavior)
- [ ] Parse `--collect-errors` flag (future extension point)
- [ ] Detect and reject unknown flags
- [ ] Read all input from `System.in`

#### Integration Requirements:
- [ ] Use `InputParser.parse(List<String>)` for parsing
- [ ] Use `MissionRunner.run(Mission, BoundaryPolicy)` for execution
- [ ] Handle `ParseException` from input parsing
- [ ] Handle `OutOfBoundsException` from mission execution

#### Output Requirements:
- [ ] Print final positions to STDOUT as "x y heading"
- [ ] Print one position per line
- [ ] Preserve rover execution order
- [ ] Print error messages to STDERR
- [ ] Include rover index in error messages
- [ ] Include line numbers in parse errors (if available)
- [ ] Include failing instruction index in execution errors

#### Exit Code Requirements:
- [ ] Exit 0 on successful execution
- [ ] Exit 1 on validation errors (`ParseException`)
- [ ] Exit 1 on execution errors (`OutOfBoundsException`)  
- [ ] Exit 2 on usage errors (unknown flags)

#### Test Coverage Requirements:
- [ ] Integration test: Canonical input → expected output
- [ ] Integration test: Unknown flag → usage error + exit 2
- [ ] Integration test: Invalid plateau → parse error + exit 1
- [ ] Integration test: OOB move in STRICT → execution error + exit 1
- [ ] Unit test: All CLI flag combinations
- [ ] Unit test: Flag validation logic
- [ ] Unit test: Boundary policy mapping

### 6. Implementation Order

1. **Create `CliArguments` class**
   - Flag parsing logic
   - Validation and error handling
   - Boundary policy mapping

2. **Create `CliArgumentsException`**
   - Usage error exception type
   - Formatted error messages

3. **Implement `Main.java`**
   - STDIN reading
   - Error handling and exit codes
   - Output formatting

4. **Create integration tests**
   - Test each acceptance criteria scenario
   - Verify exit codes and output formats

5. **Create unit tests**
   - Test CLI argument parsing
   - Test error condition handling

### 7. Assumptions and Decisions

#### Assumptions:
- Input format matches existing `InputParser` expectations
- Error messages from `InputParser` and `MissionRunner` are already appropriate for STDERR
- No need for colored output or fancy formatting
- `--collect-errors` flag is parsed but not implemented (future extension)

#### Design Decisions:
- **Separate `CliArguments` class**: Encapsulates argument parsing complexity
- **Custom exception types**: `CliArgumentsException` for usage errors
- **Static utility pattern**: Maintains consistency with existing codebase
- **No external CLI libraries**: Keeps dependencies minimal as per existing patterns

### 8. Definition of Done

#### Functional Requirements:
- [ ] All three acceptance criteria scenarios pass
- [ ] Canonical test input produces exact expected output
- [ ] Unknown flags trigger usage message and exit 2
- [ ] Invalid input triggers parse error and exit 1
- [ ] OOB moves in STRICT mode trigger execution error and exit 1

#### Quality Requirements:
- [ ] All new code follows existing code conventions
- [ ] Comprehensive Javadoc documentation
- [ ] Unit test coverage ≥90% for new components
- [ ] Integration tests cover all scenarios
- [ ] No external dependencies added
- [ ] Spotless and Checkstyle pass

#### Integration Requirements:
- [ ] `./gradlew shadowJar` creates runnable JAR
- [ ] JAR runs with canonical input and produces expected output
- [ ] Error messages are deterministic and user-friendly
- [ ] Exit codes are correct for all error conditions