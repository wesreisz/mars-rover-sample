package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositionTest {

  @Test
  void moveNorth_from_2_3_returns_2_4_preserving_heading() {
    Position start = new Position(2, 3, Direction.N);
    Position next = Move.move(start, Direction.N);

    assertThat(next).isEqualTo(new Position(2, 4, Direction.N));
    assertThat(start).isEqualTo(new Position(2, 3, Direction.N));
  }

  @Test
  void moveWest_from_5_5_returns_4_5_preserving_heading() {
    Position start = new Position(5, 5, Direction.W);
    Position next = Move.move(start, Direction.W);

    assertThat(next).isEqualTo(new Position(4, 5, Direction.W));
    assertThat(start).isEqualTo(new Position(5, 5, Direction.W));
  }

  @ParameterizedTest
  @MethodSource("allHeadingTranslations")
  void move_applies_heading_deltas_preserving_original_heading(
      int x, int y, Direction heading, int expectedX, int expectedY) {
    Position start = new Position(x, y, heading);
    Position next = Move.move(start, heading);
    assertThat(next).isEqualTo(new Position(expectedX, expectedY, heading));
    assertThat(start).isEqualTo(new Position(x, y, heading));
  }

  private static Stream<Arguments> allHeadingTranslations() {
    return Stream.of(
        Arguments.of(0, 0, Direction.N, 0, 1),
        Arguments.of(3, -2, Direction.N, 3, -1),
        Arguments.of(0, 0, Direction.E, 1, 0),
        Arguments.of(-3, 7, Direction.E, -2, 7),
        Arguments.of(0, 0, Direction.S, 0, -1),
        Arguments.of(10, 10, Direction.S, 10, 9),
        Arguments.of(0, 0, Direction.W, -1, 0),
        Arguments.of(4, -5, Direction.W, 3, -5));
  }
}
