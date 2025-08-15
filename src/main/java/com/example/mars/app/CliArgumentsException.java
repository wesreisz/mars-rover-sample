package com.example.mars.app;

/**
 * Exception thrown when CLI argument parsing fails due to invalid or unknown arguments. This
 * exception is used to signal usage errors that should result in exit code 2.
 *
 * <p>Common scenarios that trigger this exception include:
 *
 * <ul>
 *   <li>Unknown command-line flags
 *   <li>Conflicting boundary policy flags
 *   <li>Invalid flag formats or values
 * </ul>
 */
public class CliArgumentsException extends Exception {

  /**
   * Constructs a new CliArgumentsException with the specified detail message.
   *
   * @param message the detail message explaining the argument parsing error
   */
  public CliArgumentsException(String message) {
    super(message);
  }

  /**
   * Constructs a new CliArgumentsException with the specified detail message and cause.
   *
   * @param message the detail message explaining the argument parsing error
   * @param cause the underlying cause of the parsing error
   */
  public CliArgumentsException(String message, Throwable cause) {
    super(message, cause);
  }
}
