package com.dqrapps.planetarium.logic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigsTest {

    private Configs configsUnderTest;

    @BeforeEach
    void setUp() {
        configsUnderTest = new Configs(List.of(new Config("name", "longitudeDegrees",
                "longitudeMinutes", "latitudeDegrees", "dateOfObservation",
                "siderealTime", "horizon", "plotMode")));
    }

    @Test
    void testEquals() {
        // Setup
        Configs test = configsUnderTest;

        // Run the test
        final boolean result = configsUnderTest.equals(test);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = configsUnderTest.hashCode();

        // Verify the results
        assertEquals(1892572995, result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = configsUnderTest.toString();

        // Verify the results
        assertEquals("Configs(configList=[Config(" +
                "name=name, longitudeDegrees=longitudeDegrees, longitudeMinutes=longitudeMinutes, " +
                "latitudeDegrees=latitudeDegrees, dateOfObservation=dateOfObservation, siderealTime=siderealTime, " +
                "horizon=horizon, plotMode=plotMode)])", result);
    }

    @Test
    void testCanEqual() {
        // Setup
        Configs test = new Configs(List.of(new Config("name", "longitudeDegrees",
                "longitudeMinutes", "latitudeDegrees", "dateOfObservation",
                "siderealTime", "horizon", "plotMode")));

        // Run the test
        final boolean result = configsUnderTest.canEqual(test);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testBuilder() {
        // Setup

        // Run the test
        final Configs.ConfigsBuilder result = Configs.builder();

        // Verify the results
    }
}
