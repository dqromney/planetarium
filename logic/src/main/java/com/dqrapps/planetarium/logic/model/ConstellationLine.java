package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a line connecting two stars in a constellation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstellationLine {
    private String star1Name;  // Name of first star
    private String star2Name;  // Name of second star
}

