package com.dqrapps.planetarium.logic.type;

/**
 * Enumeration of available star catalogs with different star counts and detail levels.
 * Enhanced with HYG Database catalogs ranging from 1,000 to 119,000+ stars.
 */
public enum StarCatalog {

    // Original catalog (for compatibility)
    BRIGHT_STARS_166("Bright Stars", "stars.json", 166, "Original bright star catalog"),

    // HYG Database catalogs (multiple sizes)
    HYG_1000("1,000 Brightest", "stars_1k.json", 1000, "1,000 brightest stars (mag ≤ 4.5)"),
    HYG_5000("5,000 Bright Stars", "stars_5k.json", 5000, "5,000 bright stars (mag ≤ 6.0)"),
    HYG_10000("10,000 Stars", "stars_10k.json", 10000, "10,000 stars (mag ≤ 7.0)"),
    HYG_25000("25,000 Stars", "stars_25k.json", 25000, "25,000 stars (mag ≤ 8.0)"),
    HYG_50000("50,000 Stars", "stars_50k.json", 50000, "50,000 stars (mag ≤ 9.0)"),
    HYG_100000("100,000 Stars", "stars_100k.json", 100000, "100,000 stars (mag ≤ 10.0)"),
    HYG_FULL("Full HYG Catalog", "stars_full.json", 119433, "Complete HYG catalog (119,433 stars)"),

    // Alternative catalogs
    SAO_CATALOG("SAO Catalog", "stars_sao.json", 37000, "SAO star catalog"),
    SAO_FK5("SAO FK5", "stars_sao_fk5.json", 1500, "SAO FK5 subset");

    private final String displayName;
    private final String filename;
    private final int starCount;
    private final String description;

    StarCatalog(String displayName, String filename, int starCount, String description) {
        this.displayName = displayName;
        this.filename = filename;
        this.starCount = starCount;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFilename() {
        return filename;
    }

    public int getStarCount() {
        return starCount;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get display text for UI showing name and star count
     */
    public String getDisplayText() {
        if (starCount >= 100000) {
            return String.format("%s (%,d stars)", displayName, starCount);
        } else if (starCount >= 1000) {
            return String.format("%s (%,d stars)", displayName, starCount);
        } else {
            return String.format("%s (%d stars)", displayName, starCount);
        }
    }

    /**
     * Get memory usage estimate in MB
     */
    public double getEstimatedMemoryMB() {
        // Rough estimate: ~100 bytes per star object in memory
        return (starCount * 100.0) / (1024.0 * 1024.0);
    }

    /**
     * Get performance category for UI guidance
     */
    public String getPerformanceCategory() {
        if (starCount <= 1000) {
            return "Excellent";
        } else if (starCount <= 10000) {
            return "Very Good";
        } else if (starCount <= 50000) {
            return "Good";
        } else if (starCount <= 100000) {
            return "Fair (may impact performance)";
        } else {
            return "Challenging (high-end systems only)";
        }
    }

    /**
     * Check if this catalog is recommended for the given use case
     */
    public boolean isRecommendedFor(String useCase) {
        switch (useCase.toLowerCase()) {
            case "education":
            case "classroom":
                return starCount >= 1000 && starCount <= 10000;
            case "amateur":
            case "hobbyist":
                return starCount >= 5000 && starCount <= 25000;
            case "professional":
            case "research":
                return starCount >= 25000;
            case "performance":
            case "slow_computer":
                return starCount <= 5000;
            default:
                return starCount <= 10000; // Default recommendation
        }
    }
}
