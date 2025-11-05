package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Container for constellation data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Constellations {
    private List<Constellation> constellations;
}

