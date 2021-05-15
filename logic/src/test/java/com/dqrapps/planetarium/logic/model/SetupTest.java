package com.dqrapps.planetarium.logic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SetupTest {

    private Setup setupUnderTest;

    @BeforeEach
    void setUp() {
        setupUnderTest = new Setup("longitudeDegrees", "longitudeMinutes",
                "latitudeDegrees", "dateOfObservation", "siderealTime",
                "horizon", "plotMode");
    }

    @Test
    void testEquals() {
        // Setup
        Setup setup = Setup
                .builder()
                .longitudeDegrees("longitudeDegrees")
                .longitudeMinutes("longitudeMinutes")
                .latitudeDegrees("latitudeDegrees")
                .dateOfObservation("dateOfObservation")
                .siderealTime("siderealTime")
                .horizon("horizon")
                .plotMode("plotMode")
                .build();

        // Run the test
        final boolean result = setupUnderTest.equals(setup);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = setupUnderTest.hashCode();

        // Verify the results
        assertEquals(311957962, result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = setupUnderTest.toString();

        // Verify the results
        assertEquals("Setup(longitudeDegrees=longitudeDegrees, longitudeMinutes=longitudeMinutes, latitudeDegrees=latitudeDegrees, dateOfObservation=dateOfObservation, siderealTime=siderealTime, horizon=horizon, plotMode=plotMode)", result);
    }

    @Test
    void testCanEqual() {
        // Setup
        Setup setup = Setup
                .builder()
                .longitudeDegrees("longitudeDegrees")
                .longitudeMinutes("longitudeMinutes")
                .latitudeDegrees("latitudeDegrees")
                .dateOfObservation("dateOfObservation")
                .siderealTime("siderealTime")
                .horizon("horizon")
                .plotMode("plotMode")
                .build();

        // Run the test
        final boolean result = setupUnderTest.canEqual(setup);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testBuilder() {
        // Setup

        // Run the test
        final Setup.SetupBuilder result = Setup.builder();

        // Verify the results
    }
}
