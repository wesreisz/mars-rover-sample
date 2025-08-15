package com.example.mars.exec;

import com.example.mars.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.*;
import java.util.List;

/**
 * Unit tests for MissionRunner class.
 * 
 * Tests cover all acceptance criteria scenarios, boundary policy behaviors,
 * edge cases, and exception handling to ensure comprehensive coverage.
 */
class MissionRunnerTest {
    
    /**
     * Acceptance Criteria Scenario 1: Canonical example with STRICT policy
     * Two rovers execute successfully within bounds
     */
    @Test
    void shouldExecuteCanonicalExampleWithStrictPolicy() throws OutOfBoundsException {
        // Given: 5x5 plateau with two rover plans
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan1 = new RoverPlan(new Position(1, 2, Direction.N), "LMLMLMLMM");
        RoverPlan plan2 = new RoverPlan(new Position(3, 3, Direction.E), "MMRMMRMRRM");
        Mission mission = new Mission(plateau, List.of(plan1, plan2));
        
        // When: executing with STRICT policy
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);
        
        // Then: both rovers end at expected positions
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(new Position(1, 3, Direction.N));
        assertThat(result.get(1)).isEqualTo(new Position(5, 1, Direction.E));
    }
    
    /**
     * Acceptance Criteria Scenario 2: Out-of-bounds with STRICT policy throws exception
     */
    @Test
    void shouldThrowExceptionForOutOfBoundsWithStrictPolicy() {
        // Given: 5x5 plateau with rover plan that goes out of bounds
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(5, 5, Direction.N), "M");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When/Then: executing with STRICT policy should throw exception
        assertThatThrownBy(() -> MissionRunner.run(mission, BoundaryPolicy.STRICT))
            .isInstanceOf(OutOfBoundsException.class)
            .hasMessage("Rover #1 instruction 1 out of bounds from (5,5,N)");
        

    }
    
    /**
     * Acceptance Criteria Scenario 3: Out-of-bounds with IGNORE policy skips moves
     */
    @Test
    void shouldIgnoreOutOfBoundsMovesWithIgnorePolicy() throws OutOfBoundsException {
        // Given: 5x5 plateau with rover plan that attempts out-of-bounds moves
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(5, 5, Direction.N), "MRMRM");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When: executing with IGNORE policy
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.IGNORE);
        
        // Then: rover completes execution, skipping OOB moves
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Position(5, 4, Direction.S));
    }
    
    /**
     * Acceptance Criteria Scenario 4: Out-of-bounds with STOP_ON_OOB policy halts rover
     */
    @Test
    void shouldStopRoverOnOutOfBoundsWithStopPolicy() throws OutOfBoundsException {
        // Given: 5x5 plateau with rover plan that attempts out-of-bounds move
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(5, 5, Direction.N), "MRMRM");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When: executing with STOP_ON_OOB policy
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STOP_ON_OOB);
        
        // Then: rover stops at position before OOB attempt
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Position(5, 5, Direction.N));
    }
    
    /**
     * Test empty mission returns empty results
     */
    @Test
    void shouldReturnEmptyListForEmptyMission() throws OutOfBoundsException {
        // Given: mission with no rover plans
        Plateau plateau = new Plateau(5, 5);
        Mission mission = new Mission(plateau, List.of());
        
        // When: executing mission
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);
        
        // Then: empty list returned
        assertThat(result).isEmpty();
    }
    
    /**
     * Test single rover mission
     */
    @Test
    void shouldExecuteSingleRoverMission() throws OutOfBoundsException {
        // Given: mission with single rover plan
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(0, 0, Direction.N), "MRML");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When: executing mission
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);
        
        // Then: single position returned
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Position(1, 1, Direction.N));
    }
    
    /**
     * Test multiple rover plans execute sequentially
     */
    @Test
    void shouldExecuteMultipleRoversSequentially() throws OutOfBoundsException {
        // Given: mission with three rover plans
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan1 = new RoverPlan(new Position(0, 0, Direction.N), "M");
        RoverPlan plan2 = new RoverPlan(new Position(1, 1, Direction.E), "M");
        RoverPlan plan3 = new RoverPlan(new Position(2, 2, Direction.S), "M");
        Mission mission = new Mission(plateau, List.of(plan1, plan2, plan3));
        
        // When: executing mission
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);
        
        // Then: all rovers complete in order
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(new Position(0, 1, Direction.N));
        assertThat(result.get(1)).isEqualTo(new Position(2, 1, Direction.E));
        assertThat(result.get(2)).isEqualTo(new Position(2, 1, Direction.S));
    }
    
    /**
     * Test invalid instruction character throws exception
     */
    @Test
    void shouldThrowExceptionForInvalidInstruction() {
        // Given: rover plan with invalid instruction
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(0, 0, Direction.N), "MX");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When/Then: should throw IllegalArgumentException
        assertThatThrownBy(() -> MissionRunner.run(mission, BoundaryPolicy.STRICT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid instruction character: X");
    }
    
    /**
     * Test exception message format for different rover indices
     */
    @Test
    void shouldProvideCorrectExceptionMessageForSecondRover() {
        // Given: mission where second rover goes out of bounds
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan1 = new RoverPlan(new Position(0, 0, Direction.N), "M");
        RoverPlan plan2 = new RoverPlan(new Position(5, 5, Direction.E), "M");
        Mission mission = new Mission(plateau, List.of(plan1, plan2));
        
        // When/Then: exception should reference rover #2
        assertThatThrownBy(() -> MissionRunner.run(mission, BoundaryPolicy.STRICT))
            .isInstanceOf(OutOfBoundsException.class)
            .hasMessage("Rover #2 instruction 1 out of bounds from (5,5,E)");
    }
    
    /**
     * Test exception message format for different instruction indices
     */
    @Test
    void shouldProvideCorrectExceptionMessageForLaterInstruction() {
        // Given: rover that goes out of bounds on third instruction
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(3, 5, Direction.N), "LRM");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When/Then: exception should reference instruction 3
        assertThatThrownBy(() -> MissionRunner.run(mission, BoundaryPolicy.STRICT))
            .isInstanceOf(OutOfBoundsException.class)
            .hasMessage("Rover #1 instruction 3 out of bounds from (3,5,N)");
    }
    
    /**
     * Parameterized test for all boundary policies with valid missions
     */
    @ParameterizedTest
    @EnumSource(BoundaryPolicy.class)
    void shouldExecuteValidMissionWithAllPolicies(BoundaryPolicy policy) throws OutOfBoundsException {
        // Given: valid mission within bounds
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(2, 2, Direction.N), "LMRM");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When: executing with any policy
        List<Position> result = MissionRunner.run(mission, policy);
        
        // Then: same result regardless of policy
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Position(1, 3, Direction.N));
    }
    
    /**
     * Test boundary conditions - rover at edge moving within bounds
     */
    @Test
    void shouldAllowMovementAlongBoundaries() throws OutOfBoundsException {
        // Given: rover at edge position
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan = new RoverPlan(new Position(0, 0, Direction.E), "MMMMM");
        Mission mission = new Mission(plateau, List.of(plan));
        
        // When: moving along bottom edge
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STRICT);
        
        // Then: rover should reach opposite edge
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Position(5, 0, Direction.E));
    }
    
    /**
     * Test STOP_ON_OOB affects only current rover, not subsequent rovers
     */
    @Test
    void shouldContinueWithNextRoverAfterStopOnOOB() throws OutOfBoundsException {
        // Given: mission where first rover stops OOB, second continues
        Plateau plateau = new Plateau(5, 5);
        RoverPlan plan1 = new RoverPlan(new Position(5, 5, Direction.N), "MM"); // OOB on first M
        RoverPlan plan2 = new RoverPlan(new Position(0, 0, Direction.N), "M");   // Valid move
        Mission mission = new Mission(plateau, List.of(plan1, plan2));
        
        // When: executing with STOP_ON_OOB
        List<Position> result = MissionRunner.run(mission, BoundaryPolicy.STOP_ON_OOB);
        
        // Then: first rover stops, second completes
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(new Position(5, 5, Direction.N)); // Stopped at OOB
        assertThat(result.get(1)).isEqualTo(new Position(0, 1, Direction.N)); // Completed normally
    }
}
