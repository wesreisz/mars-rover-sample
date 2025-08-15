package com.example.mars.app;

import com.example.mars.domain.Mission;
import com.example.mars.domain.OutOfBoundsException;
import com.example.mars.domain.ParseException;
import com.example.mars.domain.Position;
import com.example.mars.exec.MissionRunner;
import com.example.mars.parse.InputParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class for the Mars Rover CLI. Provides command-line interface for parsing rover
 * mission input from STDIN and executing missions with configurable boundary policies.
 *
 * <p>The application supports the following command-line flags:
 *
 * <ul>
 *   <li>{@code --strict} - Fail on out-of-bounds moves (default)
 *   <li>{@code --ignore-oob} - Skip out-of-bounds moves
 *   <li>{@code --stop-on-oob} - Stop rover on out-of-bounds detection
 *   <li>{@code --fail-fast} - Stop on first error (default)
 *   <li>{@code --collect-errors} - Continue processing after errors
 * </ul>
 *
 * <p>Exit codes:
 *
 * <ul>
 *   <li>0 - Success
 *   <li>1 - Validation or execution error
 *   <li>2 - Usage error (invalid command-line arguments)
 * </ul>
 *
 * <p>Input is read from STDIN and should follow the mission format:
 *
 * <ol>
 *   <li>Plateau upper-right coordinates: "maxX maxY"
 *   <li>For each rover:
 *       <ul>
 *         <li>Starting position: "x y heading" (heading ∈ {N,E,S,W})
 *         <li>Instructions: sequence of L, R, M commands
 *       </ul>
 * </ol>
 *
 * <p>Output is printed to STDOUT as final rover positions, one per line: "x y heading"
 *
 * @see CliArguments
 * @see InputParser
 * @see MissionRunner
 */
public final class Main {

  /** Private constructor to prevent instantiation of utility class. */
  private Main() {}

  /**
   * Main entry point for the Mars Rover application.
   *
   * @param args command-line arguments for configuring execution behavior
   */
  public static void main(String[] args) {
    int exitCode = run(args, System.in);
    System.exit(exitCode);
  }

  /**
   * Testable version of the main logic that returns exit codes instead of calling System.exit.
   *
   * @param args command-line arguments
   * @param inputStream input stream to read from
   * @return exit code (0 for success, 1 for validation/execution error, 2 for usage error)
   */
  public static int run(String[] args, InputStream inputStream) {
    try {
      // Scenario 1: Parse CLI arguments
      CliArguments cli = CliArguments.parse(args);

      // Read all input from input stream
      List<String> inputLines = readFromStream(inputStream);

      // Parse mission from input
      Mission mission = InputParser.parse(inputLines);

      // Execute mission with appropriate boundary policy
      List<Position> finalPositions = MissionRunner.run(mission, cli.getBoundaryPolicy());

      // Print final positions to STDOUT
      for (Position position : finalPositions) {
        System.out.println(position.x() + " " + position.y() + " " + position.heading());
      }

      // Return success code
      return 0;

    } catch (CliArgumentsException e) {
      // Scenario 2: Usage errors (unknown flags, etc.)
      System.err.println(e.getMessage());
      return 2;

    } catch (ParseException e) {
      // Scenario 3: Validation errors (invalid input format)
      System.err.println("Parse Error: " + e.getMessage());
      return 1;

    } catch (OutOfBoundsException e) {
      // Scenario 3: Execution errors (OOB moves in STRICT mode)
      System.err.println("Execution Error: " + e.getMessage());
      return 1;

    } catch (IOException e) {
      // Input reading errors
      System.err.println("Input Error: " + e.getMessage());
      return 1;

    } catch (Exception e) {
      // Unexpected errors - should not occur with valid implementation
      System.err.println("Unexpected Error: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Reads all lines from STDIN until EOF is reached.
   *
   * @return list of input lines from STDIN
   * @throws IOException if reading from STDIN fails
   */
  private static List<String> readStdin() throws IOException {
    return readFromStream(System.in);
  }

  /**
   * Reads all lines from the given input stream until EOF is reached.
   *
   * @param inputStream the input stream to read from
   * @return list of input lines from the stream
   * @throws IOException if reading from the stream fails
   */
  private static List<String> readFromStream(InputStream inputStream) throws IOException {
    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }

    return lines;
  }
}
