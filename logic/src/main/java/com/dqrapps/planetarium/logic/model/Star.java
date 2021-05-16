package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Star {

    private Double ra;
    private Double dec;
    private Double mag;
    private String name;
}
