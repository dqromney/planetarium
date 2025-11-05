package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the calculated position of the Sun at a specific time and location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SunPosition {
    private double ra;          // Right Ascension in hours (0-24)
    private double dec;         // Declination in degrees (-90 to 90)
    private double altitude;    // Altitude above horizon in degrees
    private double azimuth;     // Azimuth in degrees (0-360, N=0, E=90)
    private boolean visible;    // True if above horizon

    /**
     * Create a SunPosition with visibility automatically determined.
     */
    public SunPosition(double ra, double dec, double altitude, double azimuth) {
        this.ra = ra;
        this.dec = dec;
        this.altitude = altitude;
        this.azimuth = azimuth;
        this.visible = altitude > 0;
    }

    /**
     * Get formatted display string for the sun's status.
     */
    public String getDisplayStatus() {
        if (visible) {
            return String.format("☀ Sun: Visible (alt: %.1f°)", altitude);
        } else {
            return String.format("☀ Sun: Below horizon (%.1f° below)", Math.abs(altitude));
        }
    }
}
