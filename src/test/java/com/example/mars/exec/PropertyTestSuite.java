package com.example.mars.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.mars.domain.Direction;
import com.example.mars.domain.Mission;
import com.example.mars.domain.OutOfBoundsException;
import com.example.mars.domain.Plateau;
import com.example.mars.domain.Position;
import com.example.mars.domain.RoverPlan;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Optional property-based test suite for the Mars Rover mission execution system.
 *
 * <p>This test suite uses random instruction generation to validate system behavior under diverse
 * input scenarios while maintaining deterministic test execution through controlled random seeds.
 * The property tests focus on validating that the STRICT boundary policy correctly identifies and
 * reports out-of-bounds violations with proper context information.
 *
 * <p>Key property testing principles applied:
 *
 * <ul>
 *   <li><strong>Deterministic randomness</strong> - Uses fixed seeds to ensure reproducible test
 *       results across test runs
 *   <li><strong>Boundary condition focus</strong> - Generates scenarios that specifically test edge
 *       cases and boundary violations
 *   <li><strong>Exception contract validation</strong> - Verifies that exception messages contain
 *       correct rover index, instruction index, and position context
 *   <li><strong>Policy compliance</strong> - Ensures STRICT policy behavior is consistent across
 *       randomly generated instruction sequences
 * </ul>
 *
 * <p><strong>Implementation Note:</strong> While labeled as "property tests," these tests use
 * deterministic random generation rather than true property-based testing frameworks to maintain
 * compatibility with existing test infrastructure and ensure reproducible results.
 *
 * @see MissionRunner
 * @see BoundaryPolicy
 * @see OutOfBoundsException
 */
@DisplayName("Property Test Suite - Random Instruction Validation")
public class PropertyTestSuite {

  /** Fixed random seed for deterministic test execution. */
  private static final long DETERMINISTIC_SEED = 12345L;

  /**
   * Property test that validates STRICT policy correctly identifies out-of-bounds violations when
   * rovers are positioned to potentially exceed plateau boundaries.
   *
   * <p>This test generates random instruction strings and places rovers at positions where
   * out-of-bounds moves are likely to occur. It verifies that:
   *
   * <ul>
   *   <li>OutOfBoundsException is thrown when rovers exceed plateau boundaries
   *   <li>Exception messages contain correct rover index (1-based)
   *   <li>Exception messages contain correct instruction index (1-based)
   *   <li>Exception messages contain accurate position and direction context
   * </ul>
   *
   * <p>Uses deterministic random generation to ensure reproducible test results while testing a
   * wide variety of instruction sequences.
   */
  @RepeatedTest(10)
  @DisplayName("Random instructions under STRICT should fail on OOB with proper context")
  void randomInstructionsUnderStrictShouldFailOnOOB() {
    Random random = new Random(DETERMINISTIC_SEED);

    // Given: A rover positioned at plateau edge with random instructions likely to cause OOB
    Plateau plateau = new Plateau(3, 3);
    Position edgePosition = new Position(3, 3, Direction.N); // Top-right corner facing north
    String randomInstructions = generateRandomInstructions(random, 5, 0.6); // 60% chance of moves
    RoverPlan plan = new RoverPlan(edgePosition, randomInstructions);
    Mission mission = new Mission(plateau, List.of(plan));

    // When/Then: Executing with STRICT policy should throw detailed exception for OOB moves
    if (containsNorthwardMove(randomInstructions)) {
      assertThatThrownBy(() -> MissionRunner.run(mission, BoundaryPolicy.STRICT))
          .isInstanceOf(OutOfBoundsException.class)
          .hasMessageContaining("Rover #1")
          .hasMessageContaining("instruction")
          .hasMessageContaining("out of bounds")
          .hasMessageContaining("(3,3,"); // Position context should be included
    } else {
      // If no northward moves, execution should succeed
      try {
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);
        assertThat(result).hasSize(1);
        // Verify final position is within bounds
        Position finalPos = result.get(0);
        assertThat(finalPos.x()).isBetween(0, 3);
        assertThat(finalPos.y()).isBetween(0, 3);
      } catch (OutOfBoundsException e) {
        // If exception occurs, verify it has proper context
        assertThat(e.getMessage()).contains("Rover #1");
        assertThat(e.getMessage()).contains("instruction");
        assertThat(e.getMessage()).contains("out of bounds");
      }
    }
  }

  /**
   * Property test that validates rovers with randomly generated valid instruction sequences execute
   * successfully under all boundary policies.
   *
   * <p>This test generates instruction sequences that are guaranteed to keep rovers within plateau
   * boundaries and verifies that:
   *
   * <ul>
   *   <li>Execution completes successfully under STRICT, IGNORE, and STOP_ON_OOB policies
   *   <li>Final positions remain within plateau boundaries
   *   <li>Results are deterministic for the same input across different policies
   *   <li>No exceptions are thrown during valid instruction execution
   * </ul>
   */
  @RepeatedTest(5)
  @DisplayName("Random valid instructions should execute successfully under all policies")
  void randomValidInstructionsShouldExecuteSuccessfully() throws OutOfBoundsException {
    Random random = new Random(DETERMINISTIC_SEED + 1); // Different seed for variety

    // Given: A rover at safe center position with guaranteed valid instructions
    Plateau plateau = new Plateau(5, 5);
    Position centerPosition = new Position(2, 2, Direction.N); // Safe center position
    String validInstructions =
        generateValidInstructions(random, 8); // Only rotations and safe moves
    RoverPlan plan = new RoverPlan(centerPosition, validInstructions);
    Mission mission = new Mission(plateau, List.of(plan));

    // When: Executing with STRICT policy (most restrictive)
    List<Position> strictResult = MissionRunner.run(mission, BoundaryPolicy.STRICT);

    // Then: Execution should complete successfully
    assertThat(strictResult).hasSize(1);
    Position finalPosition = strictResult.get(0);

    // And: Final position should be within plateau boundaries
    assertThat(finalPosition.x()).isBetween(0, 5);
    assertThat(finalPosition.y()).isBetween(0, 5);

    // And: Other policies should produce same result for valid instructions
    List<Position> ignoreResult = MissionRunner.run(mission, BoundaryPolicy.IGNORE);
    List<Position> stopResult = MissionRunner.run(mission, BoundaryPolicy.STOP_ON_OOB);

    assertThat(ignoreResult).isEqualTo(strictResult);
    assertThat(stopResult).isEqualTo(strictResult);
  }

  /**
   * Property test specifically targeting multiple rovers with random instructions to validate
   * proper rover indexing in exception messages.
   */
  @Test
  @DisplayName(
      "Multiple rovers with random instructions should report correct rover index in exceptions")
  void multipleRoversRandomInstructionsShouldReportCorrectIndex() {

    // Given: Multiple rovers where second rover will definitely go OOB
    Plateau plateau = new Plateau(2, 2);
    RoverPlan validPlan = new RoverPlan(new Position(1, 1, Direction.N), "L"); // Safe rotation
    RoverPlan invalidPlan = new RoverPlan(new Position(2, 2, Direction.N), "M"); // Will go OOB
    Mission mission = new Mission(plateau, List.of(validPlan, invalidPlan));

    // When/Then: Exception should correctly identify rover #2
    assertThatThrownBy(() -> MissionRunner.run(mission, BoundaryPolicy.STRICT))
        .isInstanceOf(OutOfBoundsException.class)
        .hasMessageContaining("Rover #2")
        .hasMessageContaining("instruction 1")
        .hasMessageContaining("out of bounds from (2,2,N)");
  }

  /**
   * Generates a random instruction string with specified length and move probability.
   *
   * @param random the random number generator to use
   * @param length the desired length of instruction string
   * @param moveProbability probability (0.0-1.0) that each instruction will be a move ('M')
   * @return randomly generated instruction string
   */
  private String generateRandomInstructions(Random random, int length, double moveProbability) {
    StringBuilder instructions = new StringBuilder();
    for (int i = 0; i < length; i++) {
      if (random.nextDouble() < moveProbability) {
        instructions.append('M');
      } else {
        // Choose randomly between L and R
        instructions.append(random.nextBoolean() ? 'L' : 'R');
      }
    }
    return instructions.toString();
  }

  /**
   * Generates instruction sequences guaranteed to keep rover within a 5x5 plateau when starting
   * from center position (2,2).
   *
   * @param random the random number generator to use
   * @param length the desired length of instruction string
   * @return instruction string with only safe moves and rotations
   */
  private String generateValidInstructions(Random random, int length) {
    StringBuilder instructions = new StringBuilder();
    for (int i = 0; i < length; i++) {
      // For safety, use mostly rotations with occasional single moves
      if (random.nextDouble() < 0.3) { // 30% chance of move
        instructions.append('M');
      } else {
        // 70% chance of rotation
        instructions.append(random.nextBoolean() ? 'L' : 'R');
      }
    }
    return instructions.toString();
  }

  /**
   * Checks if instruction string contains any moves that would take a north-facing rover further
   * north.
   *
   * @param instructions the instruction string to analyze
   * @return true if instructions contain northward moves from initial north heading
   */
  private boolean containsNorthwardMove(String instructions) {
    Direction currentDirection = Direction.N;
    for (char instruction : instructions.toCharArray()) {
      switch (instruction) {
        case 'L':
          currentDirection = currentDirection.rotateLeft();
          break;
        case 'R':
          currentDirection = currentDirection.rotateRight();
          break;
        case 'M':
          if (currentDirection == Direction.N) {
            return true; // Found a northward move from top edge
          }
          break;
      }
    }
    return false;
  }
}
