package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a constellation with its lines connecting stars.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Constellation {
    private String name;          // Full name: "Orion", "Ursa Major"
    private String abbreviation;  // IAU abbreviation: "Ori", "UMa"
    private List<ConstellationLine> lines;
}

