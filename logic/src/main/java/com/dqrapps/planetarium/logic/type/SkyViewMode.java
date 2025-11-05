package com.dqrapps.planetarium.logic.type;

/**
 * Enumeration of different sky view modes for the planetarium display.
 */
public enum SkyViewMode {
    /**
     * Single hemisphere view - shows only the portion of sky visible from current location.
     * This is the traditional planetarium view showing what you'd see looking up.
     */
    SINGLE_HEMISPHERE("Single View", "Realistic local sky view"),

    /**
     * Dual hemisphere view - shows both northern and southern celestial hemispheres side by side.
     * Provides complete view of the entire celestial sphere.
     */
    DUAL_HEMISPHERE("Dual View", "North & South hemispheres"),

    /**
     * Full sky Mercator projection - shows entire celestial sphere in rectangular format.
     * Similar to world map projections but for the sky.
     */
    FULL_SKY_MERCATOR("Full Sky", "Complete celestial sphere");

    private final String displayName;
    private final String description;

    SkyViewMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
