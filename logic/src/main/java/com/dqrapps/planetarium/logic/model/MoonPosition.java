package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the calculated position and phase of the Moon at a specific time and location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoonPosition {
    private double ra;              // Right Ascension in hours (0-24)
    private double dec;             // Declination in degrees (-90 to 90)
    private double altitude;        // Altitude above horizon in degrees
    private double azimuth;         // Azimuth in degrees (0-360, N=0, E=90)
    private double illumination;    // Phase illumination (0.0 = new, 1.0 = full)
    private String phaseName;       // "New Moon", "Waxing Crescent", etc.
    private boolean visible;        // True if above horizon

    /**
     * Create a MoonPosition with visibility automatically determined.
     */
    public MoonPosition(double ra, double dec, double altitude, double azimuth,
                       double illumination, String phaseName) {
        this.ra = ra;
        this.dec = dec;
        this.altitude = altitude;
        this.azimuth = azimuth;
        this.illumination = illumination;
        this.phaseName = phaseName;
        this.visible = altitude > 0;
    }

    /**
     * Get the moon phase emoji based on illumination.
     */
    public String getPhaseEmoji() {
        if (illumination < 0.05) return "🌑"; // New Moon
        else if (illumination < 0.25) return "🌒"; // Waxing Crescent
        else if (illumination < 0.35) return "🌓"; // First Quarter
        else if (illumination < 0.65) return "🌔"; // Waxing Gibbous
        else if (illumination < 0.85) return "🌕"; // Full Moon
        else if (illumination < 0.95) return "🌖"; // Waning Gibbous
        else if (illumination > 0.65) return "🌗"; // Last Quarter
        else return "🌘"; // Waning Crescent
    }

    /**
     * Get formatted display string for the moon's status.
     */
    public String getDisplayStatus() {
        String emoji = getPhaseEmoji();
        String status = String.format("%s %s (%.0f%% lit)", emoji, phaseName, illumination * 100);

        if (!visible) {
            status += " - Below horizon";
        }

        return status;
    }

    /**
     * Get the illumination percentage as an integer.
     */
    public int getIlluminationPercent() {
        return (int) Math.round(illumination * 100);
    }
}
