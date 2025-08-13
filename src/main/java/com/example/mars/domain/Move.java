package com.example.mars.domain;

/** Utility class for moving positions on the grid. */
public final class Move {
  private Move() {}

  /**
   * Returns a new {@link Position} translated by the provided heading's deltas, preserving the
   * original position's heading.
   *
   * @param position starting position
   * @param heading movement direction whose deltas will be applied
   * @return new position with updated coordinates and the same heading as the original
   */
  public static Position move(Position position, Direction heading) {
    int newX = position.x() + heading.dx();
    int newY = position.y() + heading.dy();
    return new Position(newX, newY, position.heading());
  }
}
