package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.model.Star;
import com.dqrapps.planetarium.logic.spatial.QuadTree;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class StarService {

    private final ObjectMapper om;
    private Stars stars;
    private QuadTree spatialIndex;

    private static StarService instance = null;
    private static final String resourceName = "/data/stars.json";
    private static final String fallbackFilename = "stars.json";

    private StarService() {
        om = new ObjectMapper();
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
                    // Update instance state (Phase 7)
                    this.stars = loadedStars;
                    buildSpatialIndex();
                    return loadedStars;
                }
            } catch (Exception e) {
                System.out.println("Could not load from resource: " + e.getMessage());
            }

            // Fall back to external file
            fileName = fallbackFilename;
        }

        // Load from external file
        loadedStars = om.readerFor(Stars.class).readValue(new File(fileName));

        // Update instance state (Phase 7)
        this.stars = loadedStars;
        buildSpatialIndex();

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
