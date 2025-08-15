package com.example.mars.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.mars.domain.Direction;
import com.example.mars.domain.Mission;
import com.example.mars.domain.ParseException;
import com.example.mars.domain.Plateau;
import com.example.mars.domain.Position;
import com.example.mars.domain.RoverPlan;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputParserTest {

  @Test
  void acceptanceCriteria_scenario1_canonicalInput() throws ParseException {
    // Given the canonical test input
    List<String> input = Arrays.asList("5 5", "1 2 N", "LMLMLMLMM", "3 3 E", "MMRMMRMRRM");

    // When I parse it
    Mission mission = InputParser.parse(input);

    // Then I get Plateau(5,5) and two RoverPlans matching the input
    assertThat(mission.plateau()).isEqualTo(new Plateau(5, 5));
    assertThat(mission.plans()).hasSize(2);

    RoverPlan plan1 = mission.plans().get(0);
    assertThat(plan1.start()).isEqualTo(new Position(1, 2, Direction.N));
    assertThat(plan1.instructions()).isEqualTo("LMLMLMLMM");

    RoverPlan plan2 = mission.plans().get(1);
    assertThat(plan2.start()).isEqualTo(new Position(3, 3, Direction.E));
    assertThat(plan2.instructions()).isEqualTo("MMRMMRMRRM");
  }

  @Test
  void acceptanceCriteria_scenario2_badPlateauLine() {
    // Given the plateau line "5 X"
    List<String> input = Arrays.asList("5 X", "1 2 N", "LRM");

    // When I parse
    // Then a ParseException is thrown
    // And the message contains Plateau line invalid (expected "X Y"): "5 X"
    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Plateau line invalid (expected \"X Y\"): \"5 X\"");
  }

  @Test
  void acceptanceCriteria_scenario3_oobStart() {
    // Given plateau "5 5" and rover start "7 1 N"
    List<String> input = Arrays.asList("5 5", "7 1 N", "LRM");

    // When I parse
    // Then a ParseException is thrown
    // And the message contains Rover #1 start out of bounds: (7,1) > plateau (5,5)
    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Rover #1 start out of bounds: (7,1) > plateau (5,5)");
  }

  @Test
  void parse_withNullInput_throwsParseException() {
    assertThatThrownBy(() -> InputParser.parse(null))
        .isInstanceOf(ParseException.class)
        .hasMessage("Input lines cannot be null");
  }

  @Test
  void parse_withEmptyInput_throwsParseException() {
    assertThatThrownBy(() -> InputParser.parse(Collections.emptyList()))
        .isInstanceOf(ParseException.class)
        .hasMessage("Input cannot be empty");
  }

  @Test
  void parse_withOnlyWhitespace_throwsParseException() {
    List<String> input = Arrays.asList("   ", "\t", "");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Input cannot be empty");
  }

  @Test
  void parse_withOnlyPlateauLine_createsEmptyMission() throws ParseException {
    List<String> input = Arrays.asList("5 5");

    Mission mission = InputParser.parse(input);

    assertThat(mission.plateau()).isEqualTo(new Plateau(5, 5));
    assertThat(mission.plans()).isEmpty();
  }

  @Test
  void parse_withOddNumberOfRoverLines_throwsParseException() {
    List<String> input = Arrays.asList("5 5", "1 2 N");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Rover specifications must come in pairs (position line + instructions line)");
  }

  @ParameterizedTest
  @ValueSource(strings = {"5", "5 5 5"})
  void parsePlateau_withInvalidFormat_throwsParseException(String plateauLine) {
    List<String> input = Arrays.asList(plateauLine);

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Plateau line invalid (expected \"X Y\")");
  }

  @Test
  void parsePlateau_withEmptyLine_throwsParseException() {
    List<String> input = Arrays.asList("");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Input cannot be empty");
  }

  @Test
  void parsePlateau_withWhitespaceLine_throwsParseException() {
    List<String> input = Arrays.asList("   ");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Input cannot be empty");
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1 5", "5 -1", "-1 -1"})
  void parsePlateau_withNegativeCoordinates_throwsParseException(String plateauLine) {
    List<String> input = Arrays.asList(plateauLine);

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Plateau coordinates must be non-negative");
  }

  @Test
  void parsePlateau_withNonNumericCoordinates_throwsParseException() {
    List<String> input = Arrays.asList("X Y");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Plateau line invalid (expected \"X Y\"): \"X Y\"");
  }

  @ParameterizedTest
  @ValueSource(strings = {"1 2", "1 2 N N", "A B C"})
  void parseRoverPosition_withInvalidFormat_throwsParseException(String positionLine) {
    List<String> input = Arrays.asList("5 5", positionLine, "LRM");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Rover #1 position invalid");
  }

  @ParameterizedTest
  @ValueSource(strings = {"1 2 X", "1 2 Z", "1 2 n"})
  void parseRoverPosition_withInvalidHeading_throwsParseException(String positionLine) {
    List<String> input = Arrays.asList("5 5", positionLine, "LRM");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Rover #1 invalid heading");
  }

  @Test
  void parseInstructions_withWhitespaceInstructions_throwsParseException() {
    List<String> input = Arrays.asList("5 5", "1 2 N", "   ");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Rover specifications must come in pairs (position line + instructions line)");
  }

  @ParameterizedTest
  @ValueSource(strings = {"LRMX", "ABC", "LRM123", "L R M"})
  void parseInstructions_withInvalidCharacters_throwsParseException(String instructions) {
    List<String> input = Arrays.asList("5 5", "1 2 N", instructions);

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessageContaining("Rover #1 invalid instructions (expected only L, R, M)");
  }

  @Test
  void parse_withMultipleRovers_indexesCorrectlyInErrors() {
    // Test that second rover gets correct index in error message
    List<String> input = Arrays.asList("5 5", "1 2 N", "LRM", "7 1 E", "M");

    assertThatThrownBy(() -> InputParser.parse(input))
        .isInstanceOf(ParseException.class)
        .hasMessage("Rover #2 start out of bounds: (7,1) > plateau (5,5)");
  }

  @Test
  void parse_withValidMultipleRovers_createsCorrectMission() throws ParseException {
    List<String> input =
        Arrays.asList("10 10", "0 0 N", "MMRMM", "5 5 E", "LMLMR", "9 9 S", "RRMLM");

    Mission mission = InputParser.parse(input);

    assertThat(mission.plateau()).isEqualTo(new Plateau(10, 10));
    assertThat(mission.plans()).hasSize(3);

    assertThat(mission.plans().get(0))
        .isEqualTo(new RoverPlan(new Position(0, 0, Direction.N), "MMRMM"));
    assertThat(mission.plans().get(1))
        .isEqualTo(new RoverPlan(new Position(5, 5, Direction.E), "LMLMR"));
    assertThat(mission.plans().get(2))
        .isEqualTo(new RoverPlan(new Position(9, 9, Direction.S), "RRMLM"));
  }

  @Test
  void parse_withMixedEmptyLines_filtersCorrectly() throws ParseException {
    List<String> input = Arrays.asList("", "5 5", "  ", "1 2 N", "", "LMLM", "\t");

    Mission mission = InputParser.parse(input);

    assertThat(mission.plateau()).isEqualTo(new Plateau(5, 5));
    assertThat(mission.plans()).hasSize(1);
    assertThat(mission.plans().get(0))
        .isEqualTo(new RoverPlan(new Position(1, 2, Direction.N), "LMLM"));
  }

  @Test
  void parse_withBoundaryPositions_allowsEdgePositions() throws ParseException {
    List<String> input = Arrays.asList("5 5", "0 0 N", "M", "5 5 S", "M");

    Mission mission = InputParser.parse(input);

    assertThat(mission.plateau()).isEqualTo(new Plateau(5, 5));
    assertThat(mission.plans()).hasSize(2);
    assertThat(mission.plans().get(0).start()).isEqualTo(new Position(0, 0, Direction.N));
    assertThat(mission.plans().get(1).start()).isEqualTo(new Position(5, 5, Direction.S));
  }

  @Test
  void parse_withMinimalValidInput_works() throws ParseException {
    List<String> input = Arrays.asList("0 0", "0 0 N", "L");

    Mission mission = InputParser.parse(input);

    assertThat(mission.plateau()).isEqualTo(new Plateau(0, 0));
    assertThat(mission.plans()).hasSize(1);
    assertThat(mission.plans().get(0))
        .isEqualTo(new RoverPlan(new Position(0, 0, Direction.N), "L"));
  }
}
