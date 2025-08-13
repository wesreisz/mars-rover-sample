package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class MissionTest {

  @Test
  void constructor_withValidInputs_setsFields() {
    Plateau plateau = new Plateau(5, 5);
    Position startPosition = new Position(1, 2, Direction.N);
    RoverPlan plan = new RoverPlan(startPosition, "LMLMLMLMM");
    List<RoverPlan> plans = Arrays.asList(plan);

    Mission mission = new Mission(plateau, plans);

    assertThat(mission.plateau()).isEqualTo(plateau);
    assertThat(mission.plans()).isEqualTo(plans);
  }

  @Test
  void constructor_withOrderedPlans_preservesOrder() {
    Plateau plateau = new Plateau(5, 5);
    RoverPlan plan1 = new RoverPlan(new Position(1, 2, Direction.N), "LMLMLMLMM");
    RoverPlan plan2 = new RoverPlan(new Position(3, 3, Direction.E), "MMRMMRMRRM");
    RoverPlan plan3 = new RoverPlan(new Position(0, 0, Direction.S), "RMMLM");
    List<RoverPlan> orderedPlans = Arrays.asList(plan1, plan2, plan3);

    Mission mission = new Mission(plateau, orderedPlans);

    assertThat(mission.plans()).containsExactly(plan1, plan2, plan3);
  }

  @Test
  void equals_withSameValues_returnsTrue() {
    Plateau plateau = new Plateau(5, 5);
    List<RoverPlan> plans = Arrays.asList(
        new RoverPlan(new Position(1, 2, Direction.N), "LMLMLMLMM")
    );

    Mission mission1 = new Mission(plateau, plans);
    Mission mission2 = new Mission(plateau, plans);

    assertThat(mission1).isEqualTo(mission2);
    assertThat(mission1.hashCode()).isEqualTo(mission2.hashCode());
  }

  @Test
  void equals_withDifferentValues_returnsFalse() {
    Plateau plateau1 = new Plateau(5, 5);
    Plateau plateau2 = new Plateau(3, 3);
    List<RoverPlan> plans1 = Arrays.asList(
        new RoverPlan(new Position(1, 2, Direction.N), "LMLMLMLMM")
    );
    List<RoverPlan> plans2 = Arrays.asList(
        new RoverPlan(new Position(2, 3, Direction.E), "MMRMMRMRRM")
    );

    Mission mission1 = new Mission(plateau1, plans1);
    Mission mission2 = new Mission(plateau2, plans2);

    assertThat(mission1).isNotEqualTo(mission2);
  }

  @Test
  void toString_includesBothFields() {
    Plateau plateau = new Plateau(5, 5);
    RoverPlan plan = new RoverPlan(new Position(1, 2, Direction.N), "LMLMLMLMM");
    List<RoverPlan> plans = Arrays.asList(plan);

    Mission mission = new Mission(plateau, plans);

    String result = mission.toString();
    assertThat(result).contains("Plateau[maxX=5, maxY=5]");
    assertThat(result).contains("Position[x=1, y=2, heading=N]");
    assertThat(result).contains("LMLMLMLMM");
  }

  @Test
  void acceptanceCriteria_scenario1_constructAndRead() {
    // Given a plateau and a list of RoverPlans
    Plateau plateau = new Plateau(5, 5);
    RoverPlan plan1 = new RoverPlan(new Position(1, 2, Direction.N), "LMLMLMLMM");
    RoverPlan plan2 = new RoverPlan(new Position(3, 3, Direction.E), "MMRMMRMRRM");
    List<RoverPlan> plans = Arrays.asList(plan1, plan2);

    // When I create a Mission
    Mission mission = new Mission(plateau, plans);

    // Then getters return the values as provided
    assertThat(mission.plateau()).isEqualTo(plateau);
    assertThat(mission.plans()).isEqualTo(plans);
    // And the order of plans is preserved
    assertThat(mission.plans()).containsExactly(plan1, plan2);
  }

  @Test
  void constructor_withEmptyPlans_allowsEmpty() {
    Plateau plateau = new Plateau(5, 5);
    List<RoverPlan> emptyPlans = Collections.emptyList();

    Mission mission = new Mission(plateau, emptyPlans);

    assertThat(mission.plateau()).isEqualTo(plateau);
    assertThat(mission.plans()).isEmpty();
  }

  @Test
  void constructor_withSinglePlan_preservesPlan() {
    Plateau plateau = new Plateau(5, 5);
    RoverPlan plan = new RoverPlan(new Position(2, 3, Direction.W), "MMRM");
    List<RoverPlan> singlePlan = Arrays.asList(plan);

    Mission mission = new Mission(plateau, singlePlan);

    assertThat(mission.plateau()).isEqualTo(plateau);
    assertThat(mission.plans()).containsExactly(plan);
  }

  @Test
  void constructor_withMultiplePlans_preservesOrder() {
    Plateau plateau = new Plateau(10, 10);
    RoverPlan plan1 = new RoverPlan(new Position(0, 0, Direction.N), "MMRMM");
    RoverPlan plan2 = new RoverPlan(new Position(5, 5, Direction.E), "LMLMR");
    RoverPlan plan3 = new RoverPlan(new Position(8, 2, Direction.S), "RRMMLM");
    RoverPlan plan4 = new RoverPlan(new Position(1, 9, Direction.W), "MLMR");
    List<RoverPlan> multiplePlans = Arrays.asList(plan1, plan2, plan3, plan4);

    Mission mission = new Mission(plateau, multiplePlans);

    assertThat(mission.plateau()).isEqualTo(plateau);
    assertThat(mission.plans()).containsExactly(plan1, plan2, plan3, plan4);
  }
}
