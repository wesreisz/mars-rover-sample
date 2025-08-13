package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoverPlanTest {

  @Test
  void constructor_withValidInputs_setsFields() {
    Position startPosition = new Position(1, 2, Direction.N);
    String instructions = "LMLMLMLMM";

    RoverPlan plan = new RoverPlan(startPosition, instructions);

    assertThat(plan.start()).isEqualTo(startPosition);
    assertThat(plan.instructions()).isEqualTo(instructions);
  }

  @Test
  void constructor_withNullPosition_allowsNull() {
    String instructions = "LMLMLMLMM";

    RoverPlan plan = new RoverPlan(null, instructions);

    assertThat(plan.start()).isNull();
    assertThat(plan.instructions()).isEqualTo(instructions);
  }

  @Test
  void constructor_withNullInstructions_allowsNull() {
    Position startPosition = new Position(3, 4, Direction.E);

    RoverPlan plan = new RoverPlan(startPosition, null);

    assertThat(plan.start()).isEqualTo(startPosition);
    assertThat(plan.instructions()).isNull();
  }

  @Test
  void constructor_withEmptyInstructions_allowsEmpty() {
    Position startPosition = new Position(0, 0, Direction.S);
    String emptyInstructions = "";

    RoverPlan plan = new RoverPlan(startPosition, emptyInstructions);

    assertThat(plan.start()).isEqualTo(startPosition);
    assertThat(plan.instructions()).isEqualTo("");
  }

  @Test
  void equals_withSameValues_returnsTrue() {
    Position position = new Position(5, 5, Direction.W);
    String instructions = "MMRMMRMRRM";

    RoverPlan plan1 = new RoverPlan(position, instructions);
    RoverPlan plan2 = new RoverPlan(position, instructions);

    assertThat(plan1).isEqualTo(plan2);
    assertThat(plan1.hashCode()).isEqualTo(plan2.hashCode());
  }

  @Test
  void equals_withDifferentValues_returnsFalse() {
    Position position1 = new Position(1, 1, Direction.N);
    Position position2 = new Position(2, 2, Direction.S);
    String instructions1 = "LMR";
    String instructions2 = "RML";

    RoverPlan plan1 = new RoverPlan(position1, instructions1);
    RoverPlan plan2 = new RoverPlan(position2, instructions2);

    assertThat(plan1).isNotEqualTo(plan2);
  }

  @Test
  void toString_includesBothFields() {
    Position position = new Position(2, 3, Direction.E);
    String instructions = "LMRM";

    RoverPlan plan = new RoverPlan(position, instructions);

    String result = plan.toString();
    assertThat(result).contains("Position[x=2, y=3, heading=E]");
    assertThat(result).contains("LMRM");
  }

  @Test
  void acceptanceCriteria_scenario1_constructAndRead() {
    // Given a Position and an instruction string
    Position startPosition = new Position(1, 2, Direction.N);
    String instructions = "LMLMLMLMM";

    // When I create a RoverPlan
    RoverPlan plan = new RoverPlan(startPosition, instructions);

    // Then getters return the provided values
    assertThat(plan.start()).isEqualTo(startPosition);
    assertThat(plan.instructions()).isEqualTo(instructions);
  }
}
