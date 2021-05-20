package com.dqrapps.planetarium.logic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StarTest {

    private Star starUnderTest;

    @BeforeEach
    void init() {
        starUnderTest = new Star(17.12,-43.1, 5.0, "ETA SCORPIO");
    }

    @Test
    void testEquals() {
        // Setup
        Star star = Star
                .builder()
                .ra(17.12)
                .dec(-43.1)
                .mag(5.0)
                .name("ETA SCORPIO")
                .build();

        // Run the test
        final boolean result = starUnderTest.equals(star);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = starUnderTest.hashCode();

        // Verify the results
        assertEquals(1278269812, result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = starUnderTest.toString();

        // Verify the results
        assertEquals("Star(ra=17.12, dec=-43.1, mag=5.0, name=ETA SCORPIO)", result);
    }

    @Test
    void testCanEqual() {
        // Setup
        Star star = Star
                .builder()
                .ra(17.12)
                .dec(-43.1)
                .mag(5.0)
                .name("ETA SCORPIO")
                .build();

        // Run the test
        final boolean result = starUnderTest.canEqual(star);

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testBuilder() {
        // Setup

        // Run the test
        final Star.StarBuilder result = Star.builder();

        // Verify the results
    }
}
