package com.example.mars.domain;

/**
 * Exception thrown when a rover attempts to move outside the defined plateau boundaries during
 * mission execution. Provides detailed context about the violation including rover identification
 * and position information.
 *
 * <p>This exception is used by the mission execution system to signal when a rover would move
 * beyond the valid plateau coordinates, allowing different boundary policies to handle such
 * situations appropriately.
 *
 * @see com.example.mars.exec.BoundaryPolicy
 * @see com.example.mars.exec.MissionRunner
 */
public class OutOfBoundsException extends Exception {

  /**
   * Constructs a new OutOfBoundsException with the specified detail message.
   *
   * @param message the detail message explaining the out-of-bounds condition, including rover
   *     identification and position context
   */
  public OutOfBoundsException(String message) {
    super(message);
  }
}
