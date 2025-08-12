package com.example.mars.domain;

/**
 * Represents the four cardinal directions for rover navigation. Supports rotation operations and
 * provides movement deltas.
 */
public enum Direction {
  /** North direction */
  N(0, 1),
  /** East direction */
  E(1, 0),
  /** South direction */
  S(0, -1),
  /** West direction */
  W(-1, 0);

  private final int deltaX;
  private final int deltaY;

  Direction(int deltaX, int deltaY) {
    this.deltaX = deltaX;
    this.deltaY = deltaY;
  }

  /**
   * Rotates the direction 90 degrees to the left (counter-clockwise). N → W → S → E → N
   *
   * @return the direction after rotating left
   */
  public Direction rotateLeft() {
    return switch (this) {
      case N -> W;
      case W -> S;
      case S -> E;
      case E -> N;
    };
  }

  /**
   * Rotates the direction 90 degrees to the right (clockwise). N → E → S → W → N
   *
   * @return the direction after rotating right
   */
  public Direction rotateRight() {
    return switch (this) {
      case N -> E;
      case E -> S;
      case S -> W;
      case W -> N;
    };
  }

  /**
   * Returns the X-axis movement delta for a forward step in this direction.
   *
   * @return the change in X coordinate (1 for East, -1 for West, 0 for North/South)
   */
  public int dx() {
    return deltaX;
  }

  /**
   * Returns the Y-axis movement delta for a forward step in this direction.
   *
   * @return the change in Y coordinate (1 for North, -1 for South, 0 for East/West)
   */
  public int dy() {
    return deltaY;
  }
}
