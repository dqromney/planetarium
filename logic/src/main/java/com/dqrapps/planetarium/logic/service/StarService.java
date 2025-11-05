package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.model.Star;
import com.dqrapps.planetarium.logic.spatial.QuadTree;
import com.dqrapps.planetarium.logic.type.StarCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class StarService {

    private static final Logger log = Logger.getLogger(StarService.class.getName());

    private final ObjectMapper om;
    private Stars stars;
    private QuadTree spatialIndex;
    private StarCatalog currentCatalog;

    private static StarService instance = null;
    private static final String resourceName = "/data/stars.json";
    private static final String fallbackFilename = "stars.json";

    private StarService() {
        om = new ObjectMapper();
        currentCatalog = StarCatalog.BRIGHT_STARS_166; // Default catalog
    }

    @SneakyThrows
    public static StarService getInstance() {
        if (instance == null) {
            instance = new StarService();
            instance.stars = instance.loadStars(null);
            instance.buildSpatialIndex();
        }
        return instance;
    }

    /**
     * Load a specific star catalog.
     */
    public void loadCatalog(StarCatalog catalog) {
        log.info("Loading catalog: " + catalog.getDisplayName() + " (" + catalog.getStarCount() + " stars)");

        long startTime = System.currentTimeMillis();

        try {
            // Load the stars first - this can throw an exception
            Stars loadedStars = loadStars(catalog.getFilename());

            // Only update if successful
            this.stars = loadedStars;
            this.currentCatalog = catalog;

            // Build spatial index for fast queries
            buildSpatialIndex();

            long loadTime = System.currentTimeMillis() - startTime;
            int actualCount = stars != null && stars.getStarList() != null ? stars.getStarList().size() : 0;

            log.info(String.format("✅ Loaded %s: %,d stars in %d ms (%.1f MB estimated)",
                catalog.getDisplayName(), actualCount, loadTime, catalog.getEstimatedMemoryMB()));

        } catch (Exception e) {
            log.warning("Failed to load catalog " + catalog.getDisplayName() + ": " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging

            // Don't change currentCatalog if we fail - keep the existing one
            // Only fallback to default if we have no stars at all
            if (stars == null) {
                try {
                    loadCatalog(StarCatalog.BRIGHT_STARS_166);
                } catch (Exception fallbackException) {
                    log.severe("Failed to load fallback catalog: " + fallbackException.getMessage());
                }
            }
        }
    }

    /**
     * Get current catalog information.
     */
    public StarCatalog getCurrentCatalog() {
        return currentCatalog;
    }

    /**
     * Get current star count.
     */
    public int getCurrentStarCount() {
        return stars != null && stars.getStarList() != null ? stars.getStarList().size() : 0;
    }

    /**
     * Build QuadTree spatial index for fast star queries.
     */
    private void buildSpatialIndex() {
        if (stars == null || stars.getStarList() == null) {
            return;
        }

        // Create quadtree covering entire celestial sphere
        // RA: 0-24 hours, Dec: -90 to +90 degrees
        QuadTree.Bounds bounds = new QuadTree.Bounds(0, 24, -90, 90);
        spatialIndex = new QuadTree(bounds, 0);

        // Insert all stars
        for (Star star : stars.getStarList()) {
            if (star.hasValidCoordinates()) {
                spatialIndex.insert(star);
            }
        }

        System.out.println("Spatial index built with " + spatialIndex.size() + " stars");
    }

    /**
     * Query stars in a rectangular region (fast with spatial index).
     */
    public List<Star> getStarsInRegion(double raMin, double raMax, double decMin, double decMax) {
        if (spatialIndex == null) {
            return stars.getStarList();  // Fallback to full list
        }

        QuadTree.Bounds bounds = new QuadTree.Bounds(raMin, raMax, decMin, decMax);
        return spatialIndex.query(bounds);
    }

    /**
     * Query stars within a circular region (cone search).
     */
    public List<Star> getStarsInRadius(double centerRA, double centerDec, double radiusDeg) {
        if (spatialIndex == null) {
            return stars.getStarList();  // Fallback
        }

        return spatialIndex.queryRadius(centerRA, centerDec, radiusDeg);
    }

    /**
     * Find nearest star to a given position.
     */
    public Star findNearestStar(double ra, double dec, double maxRadiusDeg) {
        if (spatialIndex == null) {
            return null;
        }

        return spatialIndex.findNearest(ra, dec, maxRadiusDeg);
    }

    public void saveStars(String fileName, Stars stars) throws IOException {
        om.writeValue(new File(fileName), stars);
    }

    public Stars loadStars(String fileName) throws IOException {
        Stars loadedStars;

        if (fileName == null) {
            // Try loading from classpath resource first
            try (InputStream is = getClass().getResourceAsStream(resourceName)) {
                if (is != null) {
                    loadedStars = om.readerFor(Stars.class).readValue(is);
                    return loadedStars;
                }
            } catch (Exception e) {
                System.out.println("Could not load from resource: " + e.getMessage());
            }

            // Fall back to external file
            fileName = fallbackFilename;
        }

        // Try multiple file locations
        File catalogFile = new File(fileName);

        // If not found in current directory, try gui subdirectory
        if (!catalogFile.exists()) {
            catalogFile = new File("gui/" + fileName);
            log.info("Trying alternate path: gui/" + fileName);
        }

        // If still not found, try parent directory
        if (!catalogFile.exists()) {
            catalogFile = new File("../" + fileName);
            log.info("Trying alternate path: ../" + fileName);
        }

        if (!catalogFile.exists()) {
            throw new IOException("Catalog file not found: " + fileName +
                " (searched in current dir, gui/, and ../)");
        }

        log.info("Loading stars from: " + catalogFile.getAbsolutePath());

        // Load from external file
        loadedStars = om.readerFor(Stars.class).readValue(catalogFile);

        // Don't update instance state here - let the caller do it
        // This way we can verify success before committing the change
        return loadedStars;
    }


    public boolean defaultStarsExists() {
        // Check if resource exists
        InputStream is = getClass().getResourceAsStream(resourceName);
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore
            }
            return true;
        }
        // Check if external file exists
        return new File(fallbackFilename).exists();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------

    public Stars getStars() {
        return stars;
    }

    public void setStars(Stars stars) {
        this.stars = stars;
        buildSpatialIndex();  // Rebuild index when stars change
    }
}
