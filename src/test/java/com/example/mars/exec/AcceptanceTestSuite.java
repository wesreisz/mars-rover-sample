package com.example.mars.exec;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.mars.domain.Direction;
import com.example.mars.domain.Mission;
import com.example.mars.domain.OutOfBoundsException;
import com.example.mars.domain.Plateau;
import com.example.mars.domain.Position;
import com.example.mars.domain.RoverPlan;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Comprehensive acceptance test suite providing parameterized test coverage for the Mars Rover
 * mission execution system.
 *
 * <p>This test suite focuses on acceptance scenarios including:
 *
 * <ul>
 *   <li><strong>Rotate-only path tests</strong> - Validates "LLLL" and "RRRR" instruction sequences
 *       that should return rover to original heading without position change
 *   <li><strong>Edge boundary tests</strong> - Validates rover behavior at plateau corners (0,0)
 *       and (maxX,maxY) with valid movement sequences
 *   <li><strong>Parameterized test coverage</strong> - Combines canonical, rotate-only, and
 *       boundary test scenarios into comprehensive parameterized test methods
 * </ul>
 *
 * <p>All tests use deterministic assertions with exact output line matching to ensure consistent
 * and reliable test results. The test suite integrates with existing domain objects (Mission,
 * MissionRunner, BoundaryPolicy) and follows established testing patterns (JUnit 5 + AssertJ).
 *
 * <p><strong>Coverage Goals:</strong> This suite contributes to maintaining ≥90% coverage for
 * domain and parser packages while ensuring comprehensive acceptance test coverage for critical
 * rover mission scenarios.
 *
 * @see MissionRunner
 * @see BoundaryPolicy
 * @see MissionRunnerTest
 */
@DisplayName("Acceptance Test Suite - Comprehensive Parameterized Tests")
public class AcceptanceTestSuite {

  /**
   * Tests that rovers executing rotate-only instruction paths (LLLL and RRRR) return to their
   * original heading without changing position.
   *
   * <p>This parameterized test validates that:
   *
   * <ul>
   *   <li>Four consecutive left rotations (LLLL) return rover to original heading
   *   <li>Four consecutive right rotations (RRRR) return rover to original heading
   *   <li>Rover position remains unchanged during rotation-only sequences
   *   <li>Behavior is consistent regardless of starting direction (N, E, S, W)
   * </ul>
   *
   * @param startDirection the initial direction the rover is facing
   * @param instructions the rotation instruction sequence (LLLL or RRRR)
   * @param expectedFinalDirection the expected direction after completing rotations
   */
  @ParameterizedTest
  @MethodSource("rotateOnlyPathTestData")
  @DisplayName("Should execute rotate-only paths and return to original heading")
  void shouldExecuteRotateOnlyPaths(
      Direction startDirection, String instructions, Direction expectedFinalDirection)
      throws OutOfBoundsException {
    // Given: A rover at a safe position with rotate-only instructions
    Plateau plateau = new Plateau(5, 5);
    Position startPosition = new Position(2, 2, startDirection);
    RoverPlan plan = new RoverPlan(startPosition, instructions);
    Mission mission = new Mission(plateau, List.of(plan));

    // When: Executing the rotate-only instruction sequence
    List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);

    // Then: Rover should return to original heading and maintain position
    Position finalPosition = result.get(0);
    assertThat(finalPosition.x())
        .as("Rover x position should remain unchanged during rotate-only instructions")
        .isEqualTo(startPosition.x());
    assertThat(finalPosition.y())
        .as("Rover y position should remain unchanged during rotate-only instructions")
        .isEqualTo(startPosition.y());
    assertThat(finalPosition.heading())
        .as(
            "Rover should return to original direction after completing rotation cycle: %s -> %s",
            startDirection, expectedFinalDirection)
        .isEqualTo(expectedFinalDirection);
  }

  /**
   * Tests rover execution from edge boundary positions including all four corners of the plateau.
   *
   * <p>This parameterized test validates that:
   *
   * <ul>
   *   <li>Rovers can execute valid moves from corner positions (0,0), (maxX,0), (0,maxY),
   *       (maxX,maxY)
   *   <li>Movement along edges is properly handled within boundary constraints
   *   <li>Position updates are accurate for edge and corner scenarios
   *   <li>No boundary violations occur during valid movement sequences
   * </ul>
   *
   * @param startPosition the corner or edge position where rover begins
   * @param instructions the movement instruction sequence
   * @param expectedPosition the expected final position after executing instructions
   */
  @ParameterizedTest
  @MethodSource("edgeBoundaryTestData")
  @DisplayName("Should execute valid moves from edge boundary positions")
  void shouldExecuteFromEdgeBoundaryPositions(
      Position startPosition, String instructions, Position expectedPosition)
      throws OutOfBoundsException {
    // Given: A rover starting at an edge/corner position with valid instructions
    Plateau plateau = new Plateau(5, 5);
    RoverPlan plan = new RoverPlan(startPosition, instructions);
    Mission mission = new Mission(plateau, List.of(plan));

    // When: Executing instructions from edge position
    List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);

    // Then: Rover should reach expected position and direction
    Position finalPosition = result.get(0);
    assertThat(finalPosition)
        .as("Rover should move to expected position: %s -> %s", startPosition, expectedPosition)
        .isEqualTo(expectedPosition);
  }

  /**
   * Provides test data for rotate-only path scenarios testing LLLL and RRRR instruction sequences
   * from all starting directions.
   *
   * @return stream of test arguments with start direction, instructions, and expected final
   *     direction
   */
  static Stream<Arguments> rotateOnlyPathTestData() {
    return Stream.of(
        // LLLL (left rotation cycle) tests - should return to original direction
        Arguments.of(Direction.N, "LLLL", Direction.N),
        Arguments.of(Direction.E, "LLLL", Direction.E),
        Arguments.of(Direction.S, "LLLL", Direction.S),
        Arguments.of(Direction.W, "LLLL", Direction.W),

        // RRRR (right rotation cycle) tests - should return to original direction
        Arguments.of(Direction.N, "RRRR", Direction.N),
        Arguments.of(Direction.E, "RRRR", Direction.E),
        Arguments.of(Direction.S, "RRRR", Direction.S),
        Arguments.of(Direction.W, "RRRR", Direction.W));
  }

  /**
   * Provides test data for edge boundary scenarios testing rover movement from corner and edge
   * positions on a 5x5 plateau.
   *
   * @return stream of test arguments with start position, instructions, and expected position
   */
  static Stream<Arguments> edgeBoundaryTestData() {
    return Stream.of(
        // Bottom-left corner (0,0) tests - safe moves inward
        Arguments.of(new Position(0, 0, Direction.N), "MRM", new Position(1, 1, Direction.E)),
        Arguments.of(new Position(0, 0, Direction.E), "MLM", new Position(1, 1, Direction.N)),

        // Top-right corner (5,5) tests - safe moves inward
        Arguments.of(new Position(5, 5, Direction.S), "MRM", new Position(4, 4, Direction.W)),
        Arguments.of(new Position(5, 5, Direction.W), "MLM", new Position(4, 4, Direction.S)),

        // Bottom-right corner (5,0) tests - safe moves inward
        Arguments.of(new Position(5, 0, Direction.N), "MLM", new Position(4, 1, Direction.W)),
        Arguments.of(new Position(5, 0, Direction.W), "MRM", new Position(4, 1, Direction.N)),

        // Top-left corner (0,5) tests - safe moves inward
        Arguments.of(new Position(0, 5, Direction.S), "MLM", new Position(1, 4, Direction.E)),
        Arguments.of(new Position(0, 5, Direction.E), "MRM", new Position(1, 4, Direction.S)));
  }
}
