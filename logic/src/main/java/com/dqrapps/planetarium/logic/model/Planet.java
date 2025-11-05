package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a planet, dwarf planet, or asteroid in the solar system with calculated position.
 * Enhanced to support Pluto, asteroids, hover details, and orbital paths.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Planet {

    private String name;           // Mercury, Venus, Mars, Jupiter, Saturn, Uranus, Neptune, Pluto, Ceres, etc.
    private double ra;             // Right Ascension (hours, 0-24)
    private double dec;            // Declination (degrees, -90 to +90)
    private double magnitude;      // Apparent magnitude
    private double distance;       // Distance from Earth (AU)
    private String phase;          // Phase description (e.g., "Waxing Gibbous")
    private String type;           // "planet", "dwarf_planet", "asteroid"

    // Orbital elements for path calculation
    private double semiMajorAxis;     // AU
    private double eccentricity;      // 0-1
    private double inclination;       // degrees
    private double longitudeOfAscendingNode; // degrees
    private double argumentOfPerihelion;     // degrees
    private double meanLongitude;     // degrees

    // Enhanced display properties
    private transient double screenX;
    private transient double screenY;
    private transient boolean visible;
    private transient boolean hovered;
    private transient List<OrbitalPoint> orbitalPath;

    // Hover tooltip information
    private String description;
    private String composition;
    private double diameter;           // km
    private double orbitalPeriod;      // years
    private String discoverer;
    private String discoveryDate;

    /**
     * Orbital point for drawing orbital paths
     */
    @Data
    @AllArgsConstructor
    public static class OrbitalPoint {
        private double ra;    // Right Ascension (hours)
        private double dec;   // Declination (degrees)
        private double screenX;
        private double screenY;
    }

    /**
     * Get display color for planet, dwarf planet, or asteroid
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
            case "pluto":
                return "#8B7355"; // Dark tan (dwarf planet)
            case "ceres":
                return "#C0C0C0"; // Silver (asteroid)
            case "pallas":
                return "#D3D3D3"; // Light gray (asteroid)
            case "juno":
                return "#F5F5DC"; // Beige (asteroid)
            default:
                return "#FFFFFF"; // White
        }
    }

    /**
     * Get display size for planet based on magnitude and type
     */
    public double getDisplaySize() {
        // Base size calculation from magnitude (brighter = larger)
        double baseSize = Math.max(3.0, 8.0 - magnitude);

        // Adjust based on planet/object type
        switch (name.toLowerCase()) {
            case "mercury":
                return Math.max(4.0, baseSize * 0.8); // Smallest planet
            case "venus":
                return Math.max(5.0, baseSize * 1.0); // Bright and visible
            case "mars":
                return Math.max(4.0, baseSize * 0.9); // Variable size
            case "jupiter":
                return Math.max(8.0, baseSize * 1.3); // Largest planet
            case "saturn":
                return Math.max(7.0, baseSize * 1.2); // Large with rings
            case "uranus":
                return Math.max(5.0, baseSize * 0.8); // Distant, dim
            case "neptune":
                return Math.max(5.0, baseSize * 0.8); // Most distant
            case "pluto":
                return Math.max(3.0, baseSize * 0.6); // Dwarf planet, very small
            case "ceres":
                return Math.max(3.0, baseSize * 0.5); // Largest asteroid
            case "pallas":
                return Math.max(2.5, baseSize * 0.4); // Large asteroid
            case "juno":
                return Math.max(2.5, baseSize * 0.4); // Large asteroid
            default:
                return Math.max(3.0, baseSize * 0.7); // Default for other objects
        }
    }

    /**
     * Get planet/object symbol/emoji for display
     */
    public String getSymbol() {
        switch (name.toLowerCase()) {
            case "mercury": return "☿";
            case "venus": return "♀";
            case "mars": return "♂";
            case "jupiter": return "♃";
            case "saturn": return "♄";
            case "uranus": return "♅";
            case "neptune": return "♆";
            case "pluto": return "♇";    // Dwarf planet symbol
            case "ceres": return "⚳";    // Ceres symbol
            case "pallas": return "⚴";   // Pallas symbol
            case "juno": return "⚵";     // Juno symbol
            default: return "●";
        }
    }

    /**
     * Get object type description
     */
    public String getTypeDescription() {
        if (type == null) {
            return "Unknown";
        }
        switch (type.toLowerCase()) {
            case "planet": return "Planet";
            case "dwarf_planet": return "Dwarf Planet";
            case "asteroid": return "Asteroid";
            default: return "Solar System Object";
        }
    }

    /**
     * Check if this is a dwarf planet
     */
    public boolean isDwarfPlanet() {
        return "dwarf_planet".equals(type);
    }

    /**
     * Check if this is an asteroid
     */
    public boolean isAsteroid() {
        return "asteroid".equals(type);
    }

    /**
     * Check if planet has valid coordinates for rendering
     */
    public boolean hasValidCoordinates() {
        return !Double.isNaN(ra) && !Double.isNaN(dec) && ra >= 0 && ra <= 24 && dec >= -90 && dec <= 90;
    }

    /**
     * Cache screen position for rendering optimization
     */
    public void cachePosition(double x, double y, boolean isVisible) {
        this.screenX = x;
        this.screenY = y;
        this.visible = isVisible;
    }

    /**
     * Get formatted magnitude string
     */
    public String getMagnitudeString() {
        return String.format("%.1f", magnitude);
    }

    /**
     * Get distance string in appropriate units
     */
    public String getDistanceString() {
        if (distance < 1.0) {
            return String.format("%.2f AU", distance);
        } else if (distance < 10.0) {
            return String.format("%.1f AU", distance);
        } else {
            return String.format("%.0f AU", distance);
        }
    }

    /**
     * Get diameter string with appropriate units
     */
    public String getDiameterString() {
        if (diameter == 0) return "Unknown";
        if (diameter > 10000) {
            return String.format("%.0f km", diameter);
        } else {
            return String.format("%.1f km", diameter);
        }
    }

    /**
     * Get orbital period string
     */
    public String getOrbitalPeriodString() {
        if (orbitalPeriod == 0) return "Unknown";
        if (orbitalPeriod < 1.0) {
            return String.format("%.1f days", orbitalPeriod * 365.25);
        } else if (orbitalPeriod < 10.0) {
            return String.format("%.1f years", orbitalPeriod);
        } else {
            return String.format("%.0f years", orbitalPeriod);
        }
    }

    /**
     * Get formatted hover tooltip text
     */
    public String getHoverTooltip() {
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(getSymbol()).append(" ").append(name);
        if (type != null) {
            tooltip.append(" (").append(getTypeDescription()).append(")");
        }
        tooltip.append("\n");

        tooltip.append("Magnitude: ").append(getMagnitudeString()).append("\n");
        tooltip.append("Distance: ").append(getDistanceString()).append("\n");

        if (diameter > 0) {
            tooltip.append("Diameter: ").append(getDiameterString()).append("\n");
        }

        if (orbitalPeriod > 0) {
            tooltip.append("Orbital Period: ").append(getOrbitalPeriodString()).append("\n");
        }

        if (description != null && !description.isEmpty()) {
            tooltip.append("Description: ").append(description).append("\n");
        }

        if (discoverer != null && !discoverer.isEmpty()) {
            tooltip.append("Discovered by: ").append(discoverer);
            if (discoveryDate != null && !discoveryDate.isEmpty()) {
                tooltip.append(" (").append(discoveryDate).append(")");
            }
            tooltip.append("\n");
        }

        if (composition != null && !composition.isEmpty()) {
            tooltip.append("Composition: ").append(composition);
        }

        return tooltip.toString().trim();
    }

    /**
     * Check if point is near this planet (for hover detection)
     */
    public boolean isNearPoint(double x, double y, double threshold) {
        if (!visible) return false;
        double dx = x - screenX;
        double dy = y - screenY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= threshold;
    }

    /**
     * Initialize orbital path points list
     */
    public void initializeOrbitalPath() {
        if (orbitalPath == null) {
            orbitalPath = new ArrayList<>();
        }
    }

    /**
     * Add point to orbital path
     */
    public void addOrbitalPoint(double ra, double dec, double screenX, double screenY) {
        if (orbitalPath == null) {
            initializeOrbitalPath();
        }
        orbitalPath.add(new OrbitalPoint(ra, dec, screenX, screenY));
    }

    /**
     * Clear orbital path
     */
    public void clearOrbitalPath() {
        if (orbitalPath != null) {
            orbitalPath.clear();
        }
    }

    /**
     * Get orbital path points
     */
    public List<OrbitalPoint> getOrbitalPath() {
        if (orbitalPath == null) {
            initializeOrbitalPath();
        }
        return orbitalPath;
    }

    /**
     * Check if object should show orbital path
     */
    public boolean shouldShowOrbitalPath() {
        // Show orbital paths for planets and dwarf planets, but maybe not for small asteroids
        return !"asteroid".equals(type) || magnitude < 10.0; // Only bright asteroids
    }
}

