package com.example.mars.exec;

import com.example.mars.domain.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes rover missions with configurable boundary policies.
 *
 * <p>The MissionRunner processes rover plans sequentially, executing each rover's movement
 * instructions while respecting plateau boundaries according to the specified boundary policy.
 * Different policies provide different behaviors when rovers attempt to move out of bounds.
 *
 * <p>This class follows a static utility pattern and maintains no internal state, ensuring
 * deterministic execution for the same inputs.
 *
 * @see Mission
 * @see BoundaryPolicy
 * @see OutOfBoundsException
 */
public class MissionRunner {

  /**
   * Executes a mission by processing rover plans sequentially.
   *
   * <p>Each rover plan is executed in order, with the rover starting at its designated position and
   * executing instructions character by character. The boundary policy determines behavior when a
   * rover attempts to move outside the plateau boundaries:
   *
   * <ul>
   *   <li><strong>STRICT</strong>: Throws OutOfBoundsException on first OOB attempt
   *   <li><strong>IGNORE</strong>: Skips OOB moves and continues processing
   *   <li><strong>STOP_ON_OOB</strong>: Stops processing commands for that rover at first OOB
   * </ul>
   *
   * <p>Example usage:
   *
   * <pre>{@code
   * Mission mission = new Mission(plateau, List.of(plan1, plan2));
   * List<Position> finalPositions = MissionRunner.run(mission, BoundaryPolicy.STRICT);
   * }</pre>
   *
   * @param mission the mission containing plateau and rover plans to execute
   * @param policy the boundary policy for handling out-of-bounds move attempts
   * @return list of final rover positions in execution order, one per rover plan
   * @throws OutOfBoundsException if STRICT policy is used and a rover attempts an out-of-bounds
   *     move
   * @throws IllegalArgumentException if an invalid instruction character is encountered
   */
  public static List<Position> run(Mission mission, BoundaryPolicy policy)
      throws OutOfBoundsException {
    List<Position> finalPositions = new ArrayList<>();

    for (int roverIndex = 0; roverIndex < mission.plans().size(); roverIndex++) {
      RoverPlan plan = mission.plans().get(roverIndex);
      Rover rover = new Rover(plan.start());

      String instructions = plan.instructions();
      for (int instructionIndex = 0; instructionIndex < instructions.length(); instructionIndex++) {
        char instruction = instructions.charAt(instructionIndex);

        switch (instruction) {
          case 'L':
            rover.rotateLeft();
            break;
          case 'R':
            rover.rotateRight();
            break;
          case 'M':
            Position nextPosition = rover.peekMove();
            boolean inBounds = mission.plateau().contains(nextPosition.x(), nextPosition.y());

            if (!inBounds) {
              switch (policy) {
                case STRICT:
                  Position currentPos = rover.getPosition();
                  String message =
                      String.format(
                          "Rover #%d instruction %d out of bounds from (%d,%d,%s)",
                          roverIndex + 1,
                          instructionIndex + 1,
                          currentPos.x(),
                          currentPos.y(),
                          currentPos.heading());
                  throw new OutOfBoundsException(message);
                case IGNORE:
                  // Skip this move, continue to next instruction
                  break;
                case STOP_ON_OOB:
                  // Stop processing instructions for this rover
                  instructionIndex = instructions.length(); // Break outer loop
                  break;
              }
            } else {
              rover.move();
            }
            break;
          default:
            throw new IllegalArgumentException("Invalid instruction character: " + instruction);
        }
      }

      finalPositions.add(rover.getPosition());
    }

    return finalPositions;
  }
}
