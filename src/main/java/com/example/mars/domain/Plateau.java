package com.example.mars.domain;

/**
 * Represents a rectangular plateau that defines the operational boundaries for rover movement.
 * The plateau has inclusive boundaries from (0,0) to (maxX, maxY).
 */
public record Plateau(int maxX, int maxY) {

  /**
   * Checks if the given coordinates are within the plateau boundaries.
   * Valid coordinates must satisfy: 0 ≤ x ≤ maxX and 0 ≤ y ≤ maxY
   *
   * @param x the x-coordinate to check
   * @param y the y-coordinate to check
   * @return true if the coordinates are within bounds, false otherwise
   */
  public boolean contains(int x, int y) {
    return x >= 0 && x <= maxX && y >= 0 && y <= maxY;
  }
}
