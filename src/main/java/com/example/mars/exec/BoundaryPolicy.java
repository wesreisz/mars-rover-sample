package com.example.mars.exec;

/**
 * Defines policies for handling out-of-bounds (OOB) rover movements during mission execution. These
 * policies determine the behavior when a rover attempts to move beyond the defined plateau
 * boundaries, enabling different safety and operational modes for mission control.
 */
public enum BoundaryPolicy {
  /**
   * Strict boundary enforcement. Any attempt to move out of bounds will result in an error or
   * exception, preventing the rover from executing invalid moves and maintaining strict adherence
   * to the defined operational area.
   */
  STRICT,

  /**
   * Ignore boundary violations. Out-of-bounds moves are silently ignored, allowing the rover to
   * continue operation without executing the invalid move. The rover remains at its current
   * position when an OOB move is attempted.
   */
  IGNORE,

  /**
   * Stop mission execution on out-of-bounds detection. When an OOB move is encountered, the entire
   * mission is halted, preventing any further rover commands from being executed. This provides
   * maximum safety by ensuring no unintended operations occur.
   */
  STOP_ON_OOB
}
