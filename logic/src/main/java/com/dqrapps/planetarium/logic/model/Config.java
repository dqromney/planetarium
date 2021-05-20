package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Config {
    private String name;
    private String longitudeDegrees;
    private String longitudeMinutes;
    private String latitudeDegrees;
    private String dateOfObservation;
    private String siderealTime;
    private String horizon; // North or South
    private String plotMode; // Individual or Continuous
}
