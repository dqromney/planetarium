package com.dqrapps.planetarium.logic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

    private Config configUnderTest;

    @BeforeEach
    void init() {
        configUnderTest = new Config("default","longitudeDegrees", "longitudeMinutes",
                "latitudeDegrees", "dateOfObservation", "siderealTime",
                "horizon", "plotMode");
    }

    @Test
    void testEquals() {
        // Setup
        Config config = Config
                .builder()
                .name("default")
                .longitudeDegrees("longitudeDegrees")
                .longitudeMinutes("longitudeMinutes")
                .latitudeDegrees("latitudeDegrees")
                .dateOfObservation("dateOfObservation")
                .siderealTime("siderealTime")
                .horizon("horizon")
                .plotMode("plotMode")
                .build();

        // Run the test
        final boolean result = configUnderTest.equals(config);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = configUnderTest.hashCode();

        // Verify the results
        assertEquals(-1815675797, result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = configUnderTest.toString();

        // Verify the results
        assertEquals("Config(name=default, longitudeDegrees=longitudeDegrees, longitudeMinutes=longitudeMinutes, latitudeDegrees=latitudeDegrees, dateOfObservation=dateOfObservation, siderealTime=siderealTime, horizon=horizon, plotMode=plotMode)", result);
    }

    @Test
    void testCanEqual() {
        // Setup
        Config config = Config
                .builder()
                .name("default")
                .longitudeDegrees("longitudeDegrees")
                .longitudeMinutes("longitudeMinutes")
                .latitudeDegrees("latitudeDegrees")
                .dateOfObservation("dateOfObservation")
                .siderealTime("siderealTime")
                .horizon("horizon")
                .plotMode("plotMode")
                .build();

        // Run the test
        final boolean result = configUnderTest.canEqual(config);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testBuilder() {
        // Setup

        // Run the test
        final Config.ConfigBuilder result = Config.builder();

        // Verify the results
    }
}
