package com.example.mars.domain;

/** Immutable position on the grid with an associated heading. */
public record Position(int x, int y, Direction heading) {}
