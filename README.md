# Mars Rover Navigation System

A comprehensive Java console application that simulates the navigation of multiple robotic rovers on a rectangular plateau on Mars. This application implements a complete mission control system with robust error handling, multiple boundary policies, and extensive test coverage.

## Project Overview

This project serves as a code kata demonstrating clean architecture, domain-driven design, and test-driven development using the classic Mars Rover interview problem. The application is built with Java 21, Gradle, and includes quality gates for code formatting, static analysis, and comprehensive testing.

### Problem Statement

A squad of robotic rovers are to be landed by NASA on a plateau on Mars. This plateau, which is curiously rectangular, must be navigated by the rovers so that their on-board cameras can get a complete view of the surrounding terrain to send back to Earth. 

A rover's position and location is represented by a combination of x and y coordinates and a letter representing one of the four cardinal compass points (N, E, S, W). The plateau is divided up into a grid to simplify navigation. An example position might be `0, 0, N`, which means the rover is in the bottom left corner and facing North.

To control a rover, NASA sends a simple string of letters:
- **L**: Makes the rover spin 90 degrees left without moving from its current spot
- **R**: Makes the rover spin 90 degrees right without moving from its current spot  
- **M**: Move forward one grid point, maintaining the same heading

**Note**: The square directly North from (x, y) is (x, y+1).

### Key Features

1. **Robust Input Parsing**: Validates plateau dimensions, rover starting positions, and instruction sequences with clear error messages
2. **Multiple Boundary Policies**: 
   - **STRICT**: Fail immediately on out-of-bounds moves (default)
   - **IGNORE**: Skip out-of-bounds moves and continue processing
   - **STOP_ON_OOB**: Stop processing instructions for a rover at first out-of-bounds attempt
3. **Interactive Mode**: Provides helpful prompts when running interactively
4. **Comprehensive CLI**: Support for multiple command-line flags and proper exit codes
5. **Extensive Testing**: 90%+ test coverage with unit, integration, and acceptance tests

## Architecture

The application follows clean architecture principles with clear separation of concerns:

- **`com.example.mars.app`**: CLI interface and application entry point
- **`com.example.mars.domain`**: Core domain objects (Direction, Position, Rover, Plateau, Mission)
- **`com.example.mars.parse`**: Input parsing and validation logic
- **`com.example.mars.exec`**: Mission execution engine with boundary policy handling

## Input Format

### Plateau Definition
First line: `<maxX> <maxY>` - Upper-right coordinates of the plateau (lower-left is assumed to be 0,0)

### Rover Instructions
For each rover, provide two lines:
1. Starting position: `<x> <y> <heading>` where heading is one of N, E, S, W
2. Instructions: String containing only L, R, and M characters

### Example Input
```
5 5
1 2 N
LMLMLMLMM
3 3 E
MMRMMRMRRM
```

### Expected Output
```
Rover(s) final position is: 
1 3 N
5 1 E
```

## Usage

### Command Line Options

The application supports several command-line flags to control behavior:

```bash
java -jar mars-rovers-all.jar [OPTIONS]
```

**Boundary Policy Options** (mutually exclusive):
- `--strict` (default): Fail immediately on out-of-bounds moves
- `--ignore-oob`: Skip out-of-bounds moves and continue processing
- `--stop-on-oob`: Stop processing instructions for a rover at first out-of-bounds attempt

**Error Handling Options** (mutually exclusive):
- `--fail-fast` (default): Stop execution on first error
- `--collect-errors`: Collect and report all errors before stopping

### Running the Application

#### Interactive Mode
```bash
java -jar build/libs/mars-rovers-all.jar
```
When run interactively, the application will prompt you for input format and wait for your mission data.

#### With Piped Input
```bash
echo "5 5
1 2 N
LMLMLMLMM
3 3 E
MMRMMRMRRM" | java -jar build/libs/mars-rovers-all.jar
```

#### With Input File
```bash
java -jar build/libs/mars-rovers-all.jar < mission-input.txt
```

#### With Different Boundary Policies
```bash
# Ignore out-of-bounds moves
java -jar build/libs/mars-rovers-all.jar --ignore-oob < input.txt

# Stop rover at first out-of-bounds attempt
java -jar build/libs/mars-rovers-all.jar --stop-on-oob < input.txt
```

### Exit Codes
- **0**: Success - mission completed successfully
- **1**: Validation or execution error (invalid input, out-of-bounds in strict mode)
- **2**: Usage error (invalid command-line arguments)

## Development

### Requirements
- Java 21 via SDKMAN (21.0.2-open)

### Java Setup with SDKMAN
```bash
sdk env install   # reads .sdkmanrc and installs java=21.0.2-open if missing
sdk env use       # switches current shell to 21.0.2-open
java -version     # verify 21.0.2-open
```

### Building and Testing

#### Format, Static Checks, and Build
```bash
./gradlew spotlessCheck check
```

#### Create Runnable Fat JAR
```bash
./gradlew shadowJar
```

#### Run Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

### Quality Gates

The project includes several quality gates:
- **Spotless**: Google Java Format for consistent code formatting
- **Checkstyle**: Static analysis with minimal rule set
- **JaCoCo**: Code coverage reporting (≥90% for domain and parser packages)
- **JUnit 5**: Comprehensive unit and integration tests
- **AssertJ**: Fluent assertions for better test readability

### Project Structure

```
src/
├── main/java/com/example/mars/
│   ├── app/           # CLI interface and main entry point
│   ├── domain/        # Core domain objects and business logic
│   ├── exec/          # Mission execution engine
│   └── parse/         # Input parsing and validation
└── test/java/com/example/mars/
    ├── app/           # CLI and main class tests
    ├── domain/        # Domain object unit tests
    ├── exec/          # Execution engine tests (including acceptance tests)
    └── parse/         # Parser validation tests
```

## Testing Strategy

The project implements a comprehensive testing strategy:

1. **Unit Tests**: Each domain object and utility class has focused unit tests
2. **Integration Tests**: Parser and execution engine integration scenarios
3. **Acceptance Tests**: End-to-end scenarios covering the canonical example and edge cases
4. **Property Tests**: Random instruction generation for robustness testing
5. **Error Scenario Tests**: Comprehensive coverage of all error conditions and boundary policies
