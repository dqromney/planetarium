package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Deep Sky Object (DSO) from the Messier catalog.
 * Includes galaxies, nebulae, star clusters, etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepSkyObject {

    private String messierNumber;  // M1, M31, M42, etc.
    private String name;           // Common name (e.g., "Andromeda Galaxy")
    private String type;           // Galaxy, Nebula, Open Cluster, etc.
    private double ra;             // Right Ascension (hours, 0-24)
    private double dec;            // Declination (degrees, -90 to +90)
    private double magnitude;      // Apparent magnitude
    private double size;           // Angular size in arcminutes
    private String constellation;  // Constellation it's in

    // Display properties
    private transient double screenX;
    private transient double screenY;
    private transient boolean visible;

    /**
     * Get display name (e.g., "M31 - Andromeda Galaxy")
     */
    public String getDisplayName() {
        if (name != null && !name.isEmpty()) {
            return messierNumber + " - " + name;
        }
        return messierNumber;
    }

    /**
     * Check if coordinates are valid
     */
    public boolean hasValidCoordinates() {
        return ra >= 0 && ra <= 24 && dec >= -90 && dec <= 90;
    }

    /**
     * Get color based on object type
     */
    public String getDisplayColor() {
        if (type == null) return "#CCCCCC";

        switch (type.toLowerCase()) {
            case "galaxy":
                return "#FFD700"; // Gold
            case "nebula":
            case "planetary nebula":
                return "#00FF00"; // Green
            case "open cluster":
            case "globular cluster":
                return "#87CEEB"; // Sky Blue
            case "supernova remnant":
                return "#FF6347"; // Tomato Red
            default:
                return "#CCCCCC"; // Light Gray
        }
    }
}

