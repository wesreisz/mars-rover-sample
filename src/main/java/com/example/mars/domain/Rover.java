package com.example.mars.domain;

/**
 * Represents a Mars rover that can rotate and move on a plateau. The rover maintains its current
 * position and can execute movement commands while preserving state consistency.
 */
public class Rover {
  private Position position;

  /**
   * Creates a new rover at the specified initial position.
   *
   * @param initialPosition the starting position and heading of the rover
   * @throws IllegalArgumentException if initialPosition is null
   */
  public Rover(Position initialPosition) {
    if (initialPosition == null) {
      throw new IllegalArgumentException("Initial position cannot be null");
    }
    this.position = initialPosition;
  }

  /**
   * Rotates the rover 90 degrees to the left (counter-clockwise), updating its heading while
   * preserving its coordinates.
   */
  public void rotateLeft() {
    position = new Position(position.x(), position.y(), position.heading().rotateLeft());
  }

  /**
   * Rotates the rover 90 degrees to the right (clockwise), updating its heading while preserving
   * its coordinates.
   */
  public void rotateRight() {
    position = new Position(position.x(), position.y(), position.heading().rotateRight());
  }

  /**
   * Calculates the next position if the rover were to move forward one step in its current heading,
   * without actually changing the rover's state.
   *
   * @return the position the rover would be at after moving forward
   */
  public Position peekMove() {
    int newX = position.x() + position.heading().dx();
    int newY = position.y() + position.heading().dy();
    return new Position(newX, newY, position.heading());
  }

  /** Moves the rover forward one step in its current heading direction, updating its position. */
  public void move() {
    position = peekMove();
  }

  /**
   * Returns the rover's current position.
   *
   * @return the current position and heading of the rover
   */
  public Position getPosition() {
    return position;
  }
}
