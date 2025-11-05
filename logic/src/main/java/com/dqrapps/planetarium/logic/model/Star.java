package com.dqrapps.planetarium.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Star {

    private double ra;      // Right Ascension in hours (0-24)
    private double dec;     // Declination in degrees (-90 to 90)
    private double mag;     // Apparent magnitude (brightness)
    private String name;    // Star name (optional)
    private String spectralType; // Spectral type: O, B, A, F, G, K, M (optional)

    // Cached computed values (transient = not serialized)
    private transient double screenX;
    private transient double screenY;
    private transient boolean visible;
    private transient boolean positionCached;

    /**
     * Check if this star has valid coordinates.
     */
    public boolean hasValidCoordinates() {
        return !Double.isNaN(ra) && !Double.isNaN(dec) && !Double.isNaN(mag);
    }

    /**
     * Cache screen position for faster rendering.
     */
    public void cachePosition(double x, double y, boolean isVisible) {
        this.screenX = x;
        this.screenY = y;
        this.visible = isVisible;
        this.positionCached = true;
    }

    /**
     * Clear cached position.
     */
    public void clearCache() {
        this.positionCached = false;
    }
}
