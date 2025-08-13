package com.example.mars.domain;

import java.util.List;

/**
 * Immutable record representing a complete mission configuration. Contains the operational
 * plateau and the ordered sequence of rover plans to be executed.
 *
 * <p>The mission serves as a bundle for sequential execution, preserving the order of
 * rover plans as specified by the mission operator.
 *
 * @param plateau the operational area where rovers will execute their plans
 * @param plans the ordered list of rover plans to be executed sequentially
 */
public record Mission(Plateau plateau, List<RoverPlan> plans) {}
