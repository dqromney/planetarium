package com.dqrapps.planetarium.logic.service;

import lombok.Data;

/**
 * Handles conversion of celestial coordinates (RA/Dec) to screen coordinates (x/y).
 * Implements stereographic projection for sky visualization.
 */
@Data
public class SkyProjection {

    private double centerRA;        // Center right ascension in hours (0-24)
    private double centerDec;       // Center declination in degrees (-90 to 90)
    private double fieldOfView;     // Field of view in degrees
    private double canvasWidth;     // Canvas width in pixels
    private double canvasHeight;    // Canvas height in pixels
    private double latitude;        // Observer latitude in degrees
    private double localSiderealTime; // LST in hours (0-24)

    public SkyProjection(double centerRA, double centerDec, double fieldOfView,
                         double canvasWidth, double canvasHeight) {
        this.centerRA = centerRA;
        this.centerDec = centerDec;
        this.fieldOfView = fieldOfView;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    /**
     * Convert RA/Dec to screen x/y coordinates using stereographic projection.
     *
     * @param ra Right Ascension in hours (0-24)
     * @param dec Declination in degrees (-90 to 90)
     * @return Array [x, y] screen coordinates, or null if not visible
     */
    public double[] raDecToScreen(double ra, double dec) {
        // Convert to radians
        double raRad = Math.toRadians(ra * 15.0);  // Hours to degrees to radians
        double decRad = Math.toRadians(dec);
        double centerRARad = Math.toRadians(centerRA * 15.0);
        double centerDecRad = Math.toRadians(centerDec);

        // Stereographic projection
        // Calculate angular distance from center
        double cosDist = Math.sin(decRad) * Math.sin(centerDecRad) +
                        Math.cos(decRad) * Math.cos(centerDecRad) * Math.cos(raRad - centerRARad);

        // Check if star is on visible hemisphere
        if (cosDist < 0) {
            return null; // Star is on opposite side of celestial sphere
        }

        // Calculate projection coordinates
        double k = 2.0 / (1.0 + cosDist);
        double x = k * Math.cos(decRad) * Math.sin(raRad - centerRARad);
        double y = k * (Math.cos(centerDecRad) * Math.sin(decRad) -
                       Math.sin(centerDecRad) * Math.cos(decRad) * Math.cos(raRad - centerRARad));

        // Scale to field of view and canvas size
        double scale = Math.min(canvasWidth, canvasHeight) / Math.toRadians(fieldOfView);
        double screenX = canvasWidth / 2.0 + x * scale;
        double screenY = canvasHeight / 2.0 - y * scale;

        // Check if within canvas bounds
        if (screenX < 0 || screenX > canvasWidth || screenY < 0 || screenY > canvasHeight) {
            return null;
        }

        return new double[]{screenX, screenY};
    }

    /**
     * Check if a star at given RA/Dec is within the current viewport.
     *
     * @param ra Right Ascension in hours
     * @param dec Declination in degrees
     * @return true if star is visible in viewport
     */
    public boolean isInViewport(double ra, double dec) {
        double[] coords = raDecToScreen(ra, dec);
        return coords != null;
    }
}

