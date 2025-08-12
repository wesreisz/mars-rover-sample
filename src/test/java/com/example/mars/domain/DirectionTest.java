package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class DirectionTest {

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateLeftOnce_followsExpectedMapping(Direction start) {
    Direction expected =
        switch (start) {
          case N -> Direction.W;
          case W -> Direction.S;
          case S -> Direction.E;
          case E -> Direction.N;
        };
    assertThat(start.rotateLeft()).isEqualTo(expected);
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateRightOnce_followsExpectedMapping(Direction start) {
    Direction expected =
        switch (start) {
          case N -> Direction.E;
          case E -> Direction.S;
          case S -> Direction.W;
          case W -> Direction.N;
        };
    assertThat(start.rotateRight()).isEqualTo(expected);
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateLeft_fourTimes_returnsToStart(Direction start) {
    Direction d = start;
    for (int i = 0; i < 4; i++) {
      d = d.rotateLeft();
    }
    assertThat(d).isEqualTo(start);
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void rotateRight_fourTimes_returnsToStart(Direction start) {
    Direction d = start;
    for (int i = 0; i < 4; i++) {
      d = d.rotateRight();
    }
    assertThat(d).isEqualTo(start);
  }

  @ParameterizedTest
  @MethodSource("deltaCases")
  void deltas_matchExpected(Direction direction, int expectedDx, int expectedDy) {
    assertThat(direction.dx()).isEqualTo(expectedDx);
    assertThat(direction.dy()).isEqualTo(expectedDy);
  }

  private static Stream<Arguments> deltaCases() {
    return Stream.of(
        Arguments.of(Direction.N, 0, 1),
        Arguments.of(Direction.E, 1, 0),
        Arguments.of(Direction.S, 0, -1),
        Arguments.of(Direction.W, -1, 0));
  }
}
