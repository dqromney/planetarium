package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.type.SkyViewMode;
import lombok.Data;

/**
 * Handles conversion of celestial coordinates (RA/Dec) to screen coordinates (x/y).
 * Implements multiple projection modes including single hemisphere, dual hemisphere, and full sky.
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
    private SkyViewMode viewMode;   // Current view mode

    public SkyProjection(double centerRA, double centerDec, double fieldOfView,
                         double canvasWidth, double canvasHeight) {
        this.centerRA = centerRA;
        this.centerDec = centerDec;
        this.fieldOfView = fieldOfView;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.viewMode = SkyViewMode.SINGLE_HEMISPHERE; // Default mode
    }

    /**
     * Convert RA/Dec to screen x/y coordinates using current projection mode.
     *
     * @param ra Right Ascension in hours (0-24)
     * @param dec Declination in degrees (-90 to 90)
     * @return Array [x, y] screen coordinates, or null if not visible
     */
    public double[] raDecToScreen(double ra, double dec) {
        switch (viewMode) {
            case DUAL_HEMISPHERE:
                return raDecToScreenDualHemisphere(ra, dec);
            case FULL_SKY_MERCATOR:
                return raDecToScreenMercator(ra, dec);
            case SINGLE_HEMISPHERE:
            default:
                return raDecToScreenSingle(ra, dec);
        }
    }

    /**
     * Original single hemisphere projection (stereographic).
     */
    private double[] raDecToScreenSingle(double ra, double dec) {
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
     * Dual hemisphere projection - shows both northern and southern hemispheres side by side.
     */
    private double[] raDecToScreenDualHemisphere(double ra, double dec) {
        // Convert to radians
        double raRad = Math.toRadians(ra * 15.0);
        double decRad = Math.toRadians(dec);

        // Determine which hemisphere to display in
        boolean isNorthern = dec >= 0;

        // Each hemisphere gets half the canvas width
        double hemisphereWidth = canvasWidth / 2.0;
        double hemisphereHeight = canvasHeight;

        // Project onto unit sphere, then to hemisphere
        // Use azimuthal equidistant projection for each hemisphere
        double centerDecRad = isNorthern ? Math.toRadians(90) : Math.toRadians(-90);

        // Calculate angular distance from pole
        double cosDist = Math.sin(decRad) * Math.sin(centerDecRad) +
                        Math.cos(decRad) * Math.cos(centerDecRad);

        if (cosDist < 0) {
            return null; // Point is on opposite hemisphere (shouldn't happen with our logic)
        }

        double angularDist = Math.acos(Math.max(-1, Math.min(1, cosDist)));

        // For polar projection, we need the azimuth angle
        double azimuth = Math.atan2(Math.cos(decRad) * Math.sin(raRad),
                                   Math.cos(decRad) * Math.cos(raRad));

        // Convert to polar coordinates on hemisphere
        double radius = angularDist * hemisphereHeight / Math.PI; // Scale to hemisphere

        if (radius > hemisphereHeight / 2) {
            return null; // Outside visible area
        }

        // Convert to Cartesian coordinates
        double x = radius * Math.sin(azimuth);
        double y = radius * Math.cos(azimuth);

        // Center within the appropriate hemisphere
        double hemisphereOffsetX = isNorthern ? 0 : hemisphereWidth;
        double screenX = hemisphereOffsetX + hemisphereWidth / 2.0 + x;
        double screenY = hemisphereHeight / 2.0 - y; // Flip Y axis

        // Check bounds
        if (screenX < hemisphereOffsetX || screenX > hemisphereOffsetX + hemisphereWidth ||
            screenY < 0 || screenY > hemisphereHeight) {
            return null;
        }

        return new double[]{screenX, screenY};
    }

    /**
     * Mercator projection - shows entire sky in rectangular format.
     */
    private double[] raDecToScreenMercator(double ra, double dec) {
        // Simple cylindrical projection
        // RA maps directly to X (0-24h -> 0-width)
        // Dec maps to Y with Mercator scaling (-90 to +90 -> height to 0)

        double x = (ra / 24.0) * canvasWidth;

        // Mercator projection for declination
        double decRad = Math.toRadians(dec);
        double y = canvasHeight / 2.0 - (decRad / Math.PI) * canvasHeight;

        // Wrap RA if needed
        if (x < 0) x += canvasWidth;
        if (x > canvasWidth) x -= canvasWidth;

        // Check bounds
        if (y < 0 || y > canvasHeight) {
            return null;
        }

        return new double[]{x, y};
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

    /**
     * Get hemisphere information for dual hemisphere mode.
     * @param screenX X coordinate on screen
     * @return "North" or "South" hemisphere
     */
    public String getHemisphere(double screenX) {
        if (viewMode == SkyViewMode.DUAL_HEMISPHERE) {
            return screenX < canvasWidth / 2.0 ? "North" : "South";
        }
        return "Single";
    }

    /**
     * Set the view mode and update projection accordingly.
     */
    public void setViewMode(SkyViewMode mode) {
        this.viewMode = mode;
    }
}

