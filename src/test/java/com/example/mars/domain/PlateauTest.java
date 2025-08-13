package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PlateauTest {

  @Test
  void contains_insideBounds_returnsTrue() {
    Plateau plateau = new Plateau(5, 5);

    assertThat(plateau.contains(0, 0)).isTrue();
    assertThat(plateau.contains(5, 5)).isTrue();
    assertThat(plateau.contains(3, 2)).isTrue();
  }

  @Test
  void contains_outsideBounds_returnsFalse() {
    Plateau plateau = new Plateau(5, 5);

    assertThat(plateau.contains(-1, 0)).isFalse();
    assertThat(plateau.contains(6, 0)).isFalse();
    assertThat(plateau.contains(0, 6)).isFalse();
  }

  @Test
  void contains_edgeCases_handlesCorrectly() {
    Plateau plateau = new Plateau(5, 5);

    // Test all four edges
    assertThat(plateau.contains(0, 3)).isTrue(); // left edge
    assertThat(plateau.contains(5, 3)).isTrue(); // right edge
    assertThat(plateau.contains(3, 0)).isTrue(); // bottom edge
    assertThat(plateau.contains(3, 5)).isTrue(); // top edge

    // Test all four corners
    assertThat(plateau.contains(0, 0)).isTrue(); // bottom-left
    assertThat(plateau.contains(5, 0)).isTrue(); // bottom-right
    assertThat(plateau.contains(0, 5)).isTrue(); // top-left
    assertThat(plateau.contains(5, 5)).isTrue(); // top-right
  }

  @Test
  void contains_negativeCoordinates_returnsFalse() {
    Plateau plateau = new Plateau(5, 5);

    assertThat(plateau.contains(-1, -1)).isFalse();
    assertThat(plateau.contains(-5, 3)).isFalse();
    assertThat(plateau.contains(3, -2)).isFalse();
    assertThat(plateau.contains(-10, -10)).isFalse();
  }

  @Test
  void contains_smallPlateau_worksCorrectly() {
    Plateau plateau = new Plateau(1, 1);

    assertThat(plateau.contains(0, 0)).isTrue();
    assertThat(plateau.contains(1, 1)).isTrue();
    assertThat(plateau.contains(0, 1)).isTrue();
    assertThat(plateau.contains(1, 0)).isTrue();
    assertThat(plateau.contains(2, 0)).isFalse();
    assertThat(plateau.contains(0, 2)).isFalse();
    assertThat(plateau.contains(-1, 0)).isFalse();
    assertThat(plateau.contains(0, -1)).isFalse();
  }

  @Test
  void contains_zeroPlateau_onlyContainsOrigin() {
    Plateau plateau = new Plateau(0, 0);

    assertThat(plateau.contains(0, 0)).isTrue();
    assertThat(plateau.contains(1, 0)).isFalse();
    assertThat(plateau.contains(0, 1)).isFalse();
    assertThat(plateau.contains(-1, 0)).isFalse();
    assertThat(plateau.contains(0, -1)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("plateauBoundaryTestCases")
  void contains_boundaryConditions_worksCorrectly(
      int maxX, int maxY, int testX, int testY, boolean expected) {
    Plateau plateau = new Plateau(maxX, maxY);
    assertThat(plateau.contains(testX, testY)).isEqualTo(expected);
  }

  private static Stream<Arguments> plateauBoundaryTestCases() {
    return Stream.of(
        // Plateau (10, 10) test cases
        Arguments.of(10, 10, 0, 0, true), // origin
        Arguments.of(10, 10, 10, 10, true), // max corner
        Arguments.of(10, 10, 5, 5, true), // center
        Arguments.of(10, 10, 11, 5, false), // x out of bounds
        Arguments.of(10, 10, 5, 11, false), // y out of bounds
        Arguments.of(10, 10, -1, 5, false), // negative x
        Arguments.of(10, 10, 5, -1, false), // negative y

        // Plateau (3, 7) test cases
        Arguments.of(3, 7, 0, 0, true), // origin
        Arguments.of(3, 7, 3, 7, true), // max corner
        Arguments.of(3, 7, 2, 4, true), // inside bounds
        Arguments.of(3, 7, 4, 7, false), // x out of bounds
        Arguments.of(3, 7, 3, 8, false), // y out of bounds

        // Asymmetric plateau test cases
        Arguments.of(1, 5, 1, 5, true), // max corner
        Arguments.of(1, 5, 2, 3, false), // x out of bounds
        Arguments.of(5, 1, 3, 1, true), // max y edge
        Arguments.of(5, 1, 3, 2, false) // y out of bounds
        );
  }

  @Test
  void plateau_recordProperties_workCorrectly() {
    Plateau plateau = new Plateau(8, 12);

    assertThat(plateau.maxX()).isEqualTo(8);
    assertThat(plateau.maxY()).isEqualTo(12);
  }

  @Test
  void plateau_equality_worksCorrectly() {
    Plateau plateau1 = new Plateau(5, 5);
    Plateau plateau2 = new Plateau(5, 5);
    Plateau plateau3 = new Plateau(3, 5);

    assertThat(plateau1).isEqualTo(plateau2);
    assertThat(plateau1).isNotEqualTo(plateau3);
    assertThat(plateau1.hashCode()).isEqualTo(plateau2.hashCode());
  }
}
