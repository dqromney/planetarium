package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a planet in the solar system with calculated position.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Planet {

    private String name;           // Mercury, Venus, Mars, Jupiter, Saturn
    private double ra;             // Right Ascension (hours, 0-24)
    private double dec;            // Declination (degrees, -90 to +90)
    private double magnitude;      // Apparent magnitude
    private double distance;       // Distance from Earth (AU)
    private String phase;          // Phase description (e.g., "Waxing Gibbous")

    // Display properties
    private transient double screenX;
    private transient double screenY;
    private transient boolean visible;

    /**
     * Get display color for planet
     */
    public String getDisplayColor() {
        switch (name.toLowerCase()) {
            case "mercury":
                return "#A0A0A0"; // Gray
            case "venus":
                return "#FFF8DC"; // Pale yellow (Cornsilk)
            case "mars":
                return "#CD5C5C"; // Red (Indian Red)
            case "jupiter":
                return "#DAA520"; // Goldenrod
            case "saturn":
                return "#F0E68C"; // Khaki
            case "uranus":
                return "#4FD0E0"; // Cyan
            case "neptune":
                return "#4169E1"; // Royal Blue
            default:
                return "#FFFFFF"; // White
        }
    }

    /**
     * Get display size based on magnitude
     */
    public double getDisplaySize() {
        // Brighter planets appear larger
        return Math.max(4.0, 10.0 - magnitude * 1.5);
    }

    /**
     * Check if coordinates are valid
     */
    public boolean hasValidCoordinates() {
        return ra >= 0 && ra <= 24 && dec >= -90 && dec <= 90;
    }
}

