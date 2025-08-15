package com.example.mars.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the Main application class, covering all acceptance criteria scenarios for
 * Story 11 (CLI & Error Mapping). Tests verify complete end-to-end behavior including STDIN
 * processing, CLI argument parsing, mission execution, and proper exit code handling.
 */
class MainTest {

  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  private ByteArrayOutputStream testOut;
  private ByteArrayOutputStream testErr;

  @BeforeEach
  void setUp() {
    testOut = new ByteArrayOutputStream();
    testErr = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
    System.setErr(new PrintStream(testErr));
  }

  @AfterEach
  void tearDown() {
    System.setIn(originalIn);
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  /**
   * Scenario 1 – Canonical Run Given the canonical test input via STDIN When I run java -jar
   * mars-rovers.jar Then STDOUT contains: 1 3 N and 5 1 E And the process exits with code 0
   */
  @Test
  void canonicalRun_producesExpectedOutput() {
    // Given canonical test input
    String canonicalInput = "5 5\n" + "1 2 N\n" + "LMLMLMLMM\n" + "3 3 E\n" + "MMRMMRMRRM\n";

    InputStream inputStream = new ByteArrayInputStream(canonicalInput.getBytes());

    // When running with default arguments (--strict, --fail-fast)
    int exitCode = Main.run(new String[] {}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then STDOUT contains expected final positions
    String output = testOut.toString();
    String[] lines = output.trim().split("\n");

    assertThat(lines).hasSize(2);
    assertThat(lines[0]).isEqualTo("1 3 N");
    assertThat(lines[1]).isEqualTo("5 1 E");

    // And STDERR should be empty
    assertThat(testErr.toString()).isEmpty();
  }

  /** Scenario 1 with explicit --strict flag */
  @Test
  void strictMode_canonicalRun_producesExpectedOutput() {
    // Given canonical test input
    String canonicalInput = "5 5\n" + "1 2 N\n" + "LMLMLMLMM\n" + "3 3 E\n" + "MMRMMRMRRM\n";

    InputStream inputStream = new ByteArrayInputStream(canonicalInput.getBytes());

    // When running with explicit --strict flag
    int exitCode = Main.run(new String[] {"--strict"}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then output matches expected
    String output = testOut.toString();
    String[] lines = output.trim().split("\n");

    assertThat(lines).hasSize(2);
    assertThat(lines[0]).isEqualTo("1 3 N");
    assertThat(lines[1]).isEqualTo("5 1 E");
  }

  /**
   * Scenario 2 – Usage Error Given an unknown flag --wat When I run the program Then STDERR
   * contains a usage message And the process exits with code 2
   */
  @Test
  void unknownFlag_showsUsageErrorAndExits2() {
    // Given any input (won't be processed due to argument error)
    InputStream inputStream = new ByteArrayInputStream("5 5\n".getBytes());

    // When running with unknown flag
    int exitCode = Main.run(new String[] {"--wat"}, inputStream);

    // Verify exit code 2
    assertThat(exitCode).isEqualTo(2);

    // Then STDERR contains usage message
    String errorOutput = testErr.toString();
    assertThat(errorOutput).contains("Usage: java -jar mars-rovers.jar [OPTIONS]");
    assertThat(errorOutput).contains("Options:");
    assertThat(errorOutput).contains("--strict");
    assertThat(errorOutput).contains("--ignore-oob");
    assertThat(errorOutput).contains("--stop-on-oob");
    assertThat(errorOutput).contains("--fail-fast");
    assertThat(errorOutput).contains("--collect-errors");
    assertThat(errorOutput).contains("Unknown option: --wat");

    // And STDOUT should be empty
    assertThat(testOut.toString()).isEmpty();
  }

  /**
   * Scenario 3 – Validation Error (STRICT) Given plateau "5 X" in input When I run with default
   * flags Then STDERR contains Plateau line invalid (expected "X Y"): "5 X" And the process exits
   * with code 1
   */
  @Test
  void invalidPlateauLine_showsParseErrorAndExits1() {
    // Given invalid plateau input
    String invalidInput = "5 X\n";
    InputStream inputStream = new ByteArrayInputStream(invalidInput.getBytes());

    // When running with default flags
    int exitCode = Main.run(new String[] {}, inputStream);

    // Verify exit code 1
    assertThat(exitCode).isEqualTo(1);

    // Then STDERR contains parse error message
    String errorOutput = testErr.toString();
    assertThat(errorOutput).contains("Parse Error:");
    assertThat(errorOutput).contains("Plateau line invalid (expected \"X Y\"): \"5 X\"");

    // And STDOUT should be empty
    assertThat(testOut.toString()).isEmpty();
  }

  /** Additional Scenario 3 test - Out of bounds move in STRICT mode */
  @Test
  void outOfBoundsMove_strictMode_showsExecutionErrorAndExits1() {
    // Given input with rover that will move out of bounds
    String oobInput = "5 5\n" + "0 0 S\n" + "M\n"; // Moving south from (0,0) goes out of bounds

    InputStream inputStream = new ByteArrayInputStream(oobInput.getBytes());

    // When running in strict mode (default)
    int exitCode = Main.run(new String[] {}, inputStream);

    // Verify exit code 1
    assertThat(exitCode).isEqualTo(1);

    // Then STDERR contains execution error message
    String errorOutput = testErr.toString();
    assertThat(errorOutput).contains("Execution Error:");
    assertThat(errorOutput).contains("Rover #1");
    assertThat(errorOutput).contains("instruction 1");
    assertThat(errorOutput).contains("out of bounds");
    assertThat(errorOutput).contains("(0,0,S)");

    // And STDOUT should be empty
    assertThat(testOut.toString()).isEmpty();
  }

  /** Test ignore-oob mode with out of bounds move */
  @Test
  void outOfBoundsMove_ignoreMode_skipsInvalidMoveAndContinues() {
    // Given input with rover that will attempt out of bounds move
    String oobInput =
        "5 5\n" + "0 0 S\n" + "MRM\n"; // Move south (invalid), rotate right, move east (valid)

    InputStream inputStream = new ByteArrayInputStream(oobInput.getBytes());

    // When running in ignore-oob mode
    int exitCode = Main.run(new String[] {"--ignore-oob"}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then rover skips invalid move and executes remaining commands
    String output = testOut.toString().trim();
    assertThat(output)
        .isEqualTo("0 0 W"); // Stayed at (0,0,S), rotated R to W, move west ignored (OOB)

    // And STDERR should be empty
    assertThat(testErr.toString()).isEmpty();
  }

  /** Test stop-on-oob mode with out of bounds move */
  @Test
  void outOfBoundsMove_stopMode_stopsProcessingAfterOob() {
    // Given input with rover that will attempt out of bounds move
    String oobInput =
        "5 5\n" + "0 0 S\n"
            + "MRM\n"; // Move south (invalid), rotate right, move east - should stop after first
    // command

    InputStream inputStream = new ByteArrayInputStream(oobInput.getBytes());

    // When running in stop-on-oob mode
    int exitCode = Main.run(new String[] {"--stop-on-oob"}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then rover stops processing after first invalid move
    String output = testOut.toString().trim();
    assertThat(output).isEqualTo("0 0 S"); // Remained at starting position

    // And STDERR should be empty
    assertThat(testErr.toString()).isEmpty();
  }

  /** Test with multiple rovers in different modes */
  @Test
  void multipleRovers_preservesExecutionOrder() {
    // Given input with multiple rovers
    String multiRoverInput =
        "5 5\n" + "1 2 N\n" + "LMLMLMLMM\n" + "3 3 E\n" + "MMRMMRMRRM\n" + "0 0 N\n" + "RML\n";

    InputStream inputStream = new ByteArrayInputStream(multiRoverInput.getBytes());

    // When running with default settings
    int exitCode = Main.run(new String[] {}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then output preserves rover execution order
    String output = testOut.toString();
    String[] lines = output.trim().split("\n");

    assertThat(lines).hasSize(3);
    assertThat(lines[0]).isEqualTo("1 3 N");
    assertThat(lines[1]).isEqualTo("5 1 E");
    assertThat(lines[2])
        .isEqualTo("1 0 N"); // Third rover: start N, rotate R->E, move to (1,0), rotate L->N
  }

  /**
   * Test CLI execution with rovers starting at edge boundary positions. Verifies correct STDOUT
   * formatting and proper boundary handling.
   */
  @Test
  void edgeBoundaryExecution_producesCorrectOutput() {
    // Given input with rovers starting at edge/corner positions
    String edgeBoundaryInput =
        "5 5\n" + "0 0 N\n" + // Bottom-left corner
            "MRM\n" + // Move N to (0,1), rotate R to E, move E to (1,1)
            "5 5 S\n" + // Top-right corner
            "MRM\n" + // Move S to (5,4), rotate R to W, move W to (4,4)
            "5 0 N\n" + // Bottom-right corner
            "MLM\n"; // Move N to (5,1), rotate L to W, move W to (4,1)

    InputStream inputStream = new ByteArrayInputStream(edgeBoundaryInput.getBytes());

    // When running with STRICT policy
    int exitCode = Main.run(new String[] {"--strict"}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then STDOUT contains correctly formatted edge boundary results
    String output = testOut.toString();
    String[] lines = output.trim().split("\n");

    assertThat(lines).hasSize(3);
    assertThat(lines[0]).isEqualTo("1 1 E"); // Corner rover from (0,0)
    assertThat(lines[1]).isEqualTo("4 4 W"); // Corner rover from (5,5)
    assertThat(lines[2]).isEqualTo("4 1 W"); // Corner rover from (5,0)

    // And STDERR should be empty
    assertThat(testErr.toString()).isEmpty();
  }

  /** Test CLI execution with different boundary policies for edge cases. */
  @Test
  void edgeBoundaryExecution_withDifferentPolicies() {
    // Given input with rover at edge that stays within bounds
    String edgeInput =
        "3 3\n" + "0 1 E\n" + // Left edge
            "MMM\n"; // Move E three times to reach (3,1)

    InputStream inputStream = new ByteArrayInputStream(edgeInput.getBytes());

    // When running with IGNORE policy (should behave same as STRICT for valid moves)
    int exitCode = Main.run(new String[] {"--ignore-oob"}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then output shows rover completed movement along edge
    String output = testOut.toString().trim();
    assertThat(output).isEqualTo("3 1 E");

    // And STDERR should be empty
    assertThat(testErr.toString()).isEmpty();
  }

  /** Test CLI execution with rotate-only instructions (LLLL/RRRR) that maintain position. */
  @Test
  void rotateOnlyInstructions_maintainPosition() {
    // Given input with rovers executing rotation-only sequences
    String rotateOnlyInput =
        "5 5\n" + "2 2 N\n" + // Center position
            "LLLL\n" + // Four left rotations should return to N
            "3 3 E\n" + // Another center position
            "RRRR\n"; // Four right rotations should return to E

    InputStream inputStream = new ByteArrayInputStream(rotateOnlyInput.getBytes());

    // When running with default settings
    int exitCode = Main.run(new String[] {}, inputStream);

    // Verify exit code 0
    assertThat(exitCode).isEqualTo(0);

    // Then rovers maintain position but return to original heading
    String output = testOut.toString();
    String[] lines = output.trim().split("\n");

    assertThat(lines).hasSize(2);
    assertThat(lines[0]).isEqualTo("2 2 N"); // LLLL: position unchanged, direction back to N
    assertThat(lines[1]).isEqualTo("3 3 E"); // RRRR: position unchanged, direction back to E

    // And STDERR should be empty
    assertThat(testErr.toString()).isEmpty();
  }
}
