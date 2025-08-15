package com.example.mars.parse;

import com.example.mars.domain.Direction;
import com.example.mars.domain.Mission;
import com.example.mars.domain.ParseException;
import com.example.mars.domain.Plateau;
import com.example.mars.domain.Position;
import com.example.mars.domain.RoverPlan;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing mission input text into domain objects. Handles validation of plateau
 * specifications, rover positions, and instruction sequences according to mission input format
 * requirements.
 *
 * <p>The expected input format is:
 *
 * <ol>
 *   <li>First non-empty line: plateau upper-right coordinates as "maxX maxY"
 *   <li>Repeated rover blocks consisting of:
 *       <ul>
 *         <li>Rover start position: "x y heading" where heading ∈ {N,E,S,W}
 *         <li>Rover instructions: sequence of L, R, M commands
 *       </ul>
 * </ol>
 */
public final class InputParser {

  /** Private constructor to prevent instantiation of utility class. */
  private InputParser() {}

  /**
   * Parses mission input lines into a Mission object containing the operational plateau and rover
   * plans.
   *
   * @param lines the input lines containing plateau and rover specifications
   * @return a Mission object with validated plateau and rover plans
   * @throws ParseException if the input format is invalid or contains logical errors
   */
  public static Mission parse(List<String> lines) throws ParseException {
    if (lines == null) {
      throw new ParseException("Input lines cannot be null");
    }

    if (lines.isEmpty()) {
      throw new ParseException("Input cannot be empty");
    }

    // Filter out empty lines
    List<String> nonEmptyLines =
        lines.stream().filter(line -> line != null && !line.trim().isEmpty()).toList();

    if (nonEmptyLines.isEmpty()) {
      throw new ParseException("Input cannot be empty");
    }

    // Parse plateau from first non-empty line
    Plateau plateau = parsePlateau(nonEmptyLines.get(0));

    // Parse rover plans from remaining lines
    List<RoverPlan> roverPlans =
        parseRoverPlans(nonEmptyLines.subList(1, nonEmptyLines.size()), plateau);

    return new Mission(plateau, roverPlans);
  }

  /**
   * Parses plateau specification from input line.
   *
   * @param plateauLine the line containing plateau coordinates
   * @return validated Plateau object
   * @throws ParseException if plateau specification is invalid
   */
  private static Plateau parsePlateau(String plateauLine) throws ParseException {
    String[] parts = plateauLine.trim().split("\\s+");

    if (parts.length != 2) {
      throw new ParseException("Plateau line invalid (expected \"X Y\"): \"" + plateauLine + "\"");
    }

    try {
      int maxX = Integer.parseInt(parts[0]);
      int maxY = Integer.parseInt(parts[1]);

      if (maxX < 0 || maxY < 0) {
        throw new ParseException("Plateau coordinates must be non-negative: " + maxX + " " + maxY);
      }

      return new Plateau(maxX, maxY);
    } catch (NumberFormatException e) {
      throw new ParseException(
          "Plateau line invalid (expected \"X Y\"): \"" + plateauLine + "\"", e);
    }
  }

  /**
   * Parses rover plans from remaining input lines.
   *
   * @param roverLines the lines containing rover specifications
   * @param plateau the operational plateau for boundary validation
   * @return list of validated RoverPlan objects
   * @throws ParseException if rover specifications are invalid
   */
  private static List<RoverPlan> parseRoverPlans(List<String> roverLines, Plateau plateau)
      throws ParseException {
    if (roverLines.size() % 2 != 0) {
      throw new ParseException(
          "Rover specifications must come in pairs (position line + instructions line)");
    }

    List<RoverPlan> roverPlans = new ArrayList<>();

    for (int i = 0; i < roverLines.size(); i += 2) {
      int roverIndex = (i / 2) + 1; // 1-indexed for error messages
      String positionLine = roverLines.get(i);
      String instructionsLine = roverLines.get(i + 1);

      Position startPosition = parseRoverPosition(positionLine, roverIndex);
      String instructions = parseInstructions(instructionsLine, roverIndex);

      // Validate rover start position is within plateau bounds
      if (!plateau.contains(startPosition.x(), startPosition.y())) {
        throw new ParseException(
            "Rover #"
                + roverIndex
                + " start out of bounds: ("
                + startPosition.x()
                + ","
                + startPosition.y()
                + ") > plateau ("
                + plateau.maxX()
                + ","
                + plateau.maxY()
                + ")");
      }

      roverPlans.add(new RoverPlan(startPosition, instructions));
    }

    return roverPlans;
  }

  /**
   * Parses rover starting position from input line.
   *
   * @param positionLine the line containing rover position and heading
   * @param roverIndex the rover number for error reporting (1-indexed)
   * @return validated Position object
   * @throws ParseException if position specification is invalid
   */
  private static Position parseRoverPosition(String positionLine, int roverIndex)
      throws ParseException {
    String[] parts = positionLine.trim().split("\\s+");

    if (parts.length != 3) {
      throw new ParseException(
          "Rover #"
              + roverIndex
              + " position invalid (expected \"X Y HEADING\"): \""
              + positionLine
              + "\"");
    }

    try {
      int x = Integer.parseInt(parts[0]);
      int y = Integer.parseInt(parts[1]);
      String headingStr = parts[2];

      Direction heading;
      try {
        heading = Direction.valueOf(headingStr);
      } catch (IllegalArgumentException e) {
        throw new ParseException(
            "Rover #"
                + roverIndex
                + " invalid heading (expected N, E, S, or W): \""
                + headingStr
                + "\"",
            e);
      }

      return new Position(x, y, heading);
    } catch (NumberFormatException e) {
      throw new ParseException(
          "Rover #"
              + roverIndex
              + " position invalid (expected \"X Y HEADING\"): \""
              + positionLine
              + "\"",
          e);
    }
  }

  /**
   * Parses and validates rover instruction sequence.
   *
   * @param instructionsLine the line containing rover instructions
   * @param roverIndex the rover number for error reporting (1-indexed)
   * @return validated instruction string
   * @throws ParseException if instructions are invalid
   */
  private static String parseInstructions(String instructionsLine, int roverIndex)
      throws ParseException {
    String instructions = instructionsLine.trim();

    if (instructions.isEmpty()) {
      throw new ParseException("Rover #" + roverIndex + " instructions cannot be empty");
    }

    // Validate that instructions contain only L, R, M characters
    if (!instructions.matches("[LRM]+")) {
      throw new ParseException(
          "Rover #"
              + roverIndex
              + " invalid instructions (expected only L, R, M): \""
              + instructions
              + "\"");
    }

    return instructions;
  }
}
