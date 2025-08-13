package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class RoverTest {

  @Test
  void constructor_withNullPosition_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Rover(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Initial position cannot be null");
  }

  @Test
  void constructor_withValidPosition_setsPosition() {
    Position initialPosition = new Position(1, 2, Direction.N);
    Rover rover = new Rover(initialPosition);
    assertThat(rover.getPosition()).isEqualTo(initialPosition);
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateLeft_updatesHeadingCorrectly(Direction startHeading) {
    Position initialPosition = new Position(5, 5, startHeading);
    Rover rover = new Rover(initialPosition);

    rover.rotateLeft();

    Direction expectedHeading = startHeading.rotateLeft();
    assertThat(rover.getPosition()).isEqualTo(new Position(5, 5, expectedHeading));
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateRight_updatesHeadingCorrectly(Direction startHeading) {
    Position initialPosition = new Position(3, 7, startHeading);
    Rover rover = new Rover(initialPosition);

    rover.rotateRight();

    Direction expectedHeading = startHeading.rotateRight();
    assertThat(rover.getPosition()).isEqualTo(new Position(3, 7, expectedHeading));
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateLeft_fourTimes_returnsToOriginalHeading(Direction startHeading) {
    Position initialPosition = new Position(0, 0, startHeading);
    Rover rover = new Rover(initialPosition);

    rover.rotateLeft();
    rover.rotateLeft();
    rover.rotateLeft();
    rover.rotateLeft();

    assertThat(rover.getPosition()).isEqualTo(initialPosition);
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateRight_fourTimes_returnsToOriginalHeading(Direction startHeading) {
    Position initialPosition = new Position(0, 0, startHeading);
    Rover rover = new Rover(initialPosition);

    rover.rotateRight();
    rover.rotateRight();
    rover.rotateRight();
    rover.rotateRight();

    assertThat(rover.getPosition()).isEqualTo(initialPosition);
  }

  @ParameterizedTest
  @MethodSource("peekMoveTestCases")
  void peekMove_calculatesNextPositionWithoutMutating(
      int x, int y, Direction heading, int expectedX, int expectedY) {
    Position initialPosition = new Position(x, y, heading);
    Rover rover = new Rover(initialPosition);

    Position peekedPosition = rover.peekMove();

    assertThat(peekedPosition).isEqualTo(new Position(expectedX, expectedY, heading));
    assertThat(rover.getPosition()).isEqualTo(initialPosition); // Verify no mutation
  }

  @ParameterizedTest
  @MethodSource("moveTestCases")
  void move_updatesRoverPosition(int x, int y, Direction heading, int expectedX, int expectedY) {
    Position initialPosition = new Position(x, y, heading);
    Rover rover = new Rover(initialPosition);

    rover.move();

    assertThat(rover.getPosition()).isEqualTo(new Position(expectedX, expectedY, heading));
  }

  @Test
  void acceptanceCriteria_scenario1_peekDoesNotMutate() {
    // Given a rover at (1,1,N)
    Rover rover = new Rover(new Position(1, 1, Direction.N));

    // When I call peekMove()
    Position peekedPosition = rover.peekMove();

    // Then I get (1,2,N)
    assertThat(peekedPosition).isEqualTo(new Position(1, 2, Direction.N));
    // And the rover's internal position remains (1,1,N)
    assertThat(rover.getPosition()).isEqualTo(new Position(1, 1, Direction.N));
  }

  @Test
  void acceptanceCriteria_scenario2_moveUpdatesState() {
    // Given a rover at (1,1,N)
    Rover rover = new Rover(new Position(1, 1, Direction.N));

    // When I call move()
    rover.move();

    // Then the rover's position becomes (1,2,N)
    assertThat(rover.getPosition()).isEqualTo(new Position(1, 2, Direction.N));
  }

  private static Stream<Arguments> peekMoveTestCases() {
    return Stream.of(
        Arguments.of(1, 1, Direction.N, 1, 2),
        Arguments.of(2, 3, Direction.E, 3, 3),
        Arguments.of(4, 5, Direction.S, 4, 4),
        Arguments.of(6, 7, Direction.W, 5, 7),
        Arguments.of(0, 0, Direction.N, 0, 1),
        Arguments.of(0, 0, Direction.E, 1, 0),
        Arguments.of(5, 5, Direction.S, 5, 4),
        Arguments.of(3, 2, Direction.W, 2, 2));
  }

  private static Stream<Arguments> moveTestCases() {
    return Stream.of(
        Arguments.of(1, 1, Direction.N, 1, 2),
        Arguments.of(2, 3, Direction.E, 3, 3),
        Arguments.of(4, 5, Direction.S, 4, 4),
        Arguments.of(6, 7, Direction.W, 5, 7),
        Arguments.of(0, 0, Direction.N, 0, 1),
        Arguments.of(0, 0, Direction.E, 1, 0),
        Arguments.of(5, 5, Direction.S, 5, 4),
        Arguments.of(3, 2, Direction.W, 2, 2));
  }
}
