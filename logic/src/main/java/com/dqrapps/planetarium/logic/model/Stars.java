package com.dqrapps.planetarium.logic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// http://cdsarc.u-strasbg.fr/viz-bin/Cat?I/239
// http://simbad.u-strasbg.fr/simbad/sim-tap
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stars {

    @JsonProperty("objects")
    private List<Star> starList = new ArrayList<>();
}
