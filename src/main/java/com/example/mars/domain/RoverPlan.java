package com.example.mars.domain;

/**
 * Immutable record representing a single rover's mission plan. Contains the rover's starting
 * position and the sequence of instructions to be executed.
 *
 * <p>This is a pure data holder with no validation logic - instruction validation is handled by the
 * parser components during mission processing.
 *
 * @param start the initial position and heading of the rover
 * @param instructions the sequence of movement commands (e.g., "LMLMLMLMM")
 */
public record RoverPlan(Position start, String instructions) {}
