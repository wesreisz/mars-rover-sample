package com.example.mars.domain;

/**
 * Exception thrown during mission input parsing when the input format is invalid or contains
 * logical errors. This exception provides detailed error messages to help mission operators
 * identify and correct input problems.
 *
 * <p>Common scenarios that trigger this exception include:
 *
 * <ul>
 *   <li>Invalid plateau coordinates (non-numeric, negative values)
 *   <li>Malformed rover position or heading specifications
 *   <li>Invalid instruction sequences containing unsupported commands
 *   <li>Rover starting positions outside the defined plateau boundaries
 * </ul>
 */
public class ParseException extends Exception {

  /**
   * Constructs a new ParseException with the specified detail message.
   *
   * @param message the detail message explaining the parsing error
   */
  public ParseException(String message) {
    super(message);
  }

  /**
   * Constructs a new ParseException with the specified detail message and cause.
   *
   * @param message the detail message explaining the parsing error
   * @param cause the underlying cause of the parsing error
   */
  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
