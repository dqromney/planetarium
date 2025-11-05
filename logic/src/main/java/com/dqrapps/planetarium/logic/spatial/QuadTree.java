package com.dqrapps.planetarium.logic.spatial;

import com.dqrapps.planetarium.logic.model.Star;
import java.util.ArrayList;
import java.util.List;

/**
 * QuadTree spatial index for efficient star queries.
 * Divides celestial sphere into quadrants for O(log n) lookups.
 */
public class QuadTree {

    private static final int MAX_CAPACITY = 50;  // Max stars per node before split
    private static final int MAX_DEPTH = 8;       // Max tree depth

    private final Bounds bounds;
    private final int depth;
    private final List<Star> stars;
    private QuadTree[] children;  // NE, NW, SE, SW
    private boolean divided;

    /**
     * Bounds for a region of the sky.
     */
    public static class Bounds {
        public final double raMin;
        public final double raMax;
        public final double decMin;
        public final double decMax;

        public Bounds(double raMin, double raMax, double decMin, double decMax) {
            this.raMin = raMin;
            this.raMax = raMax;
            this.decMin = decMin;
            this.decMax = decMax;
        }

        public boolean contains(double ra, double dec) {
            return ra >= raMin && ra <= raMax && dec >= decMin && dec <= decMax;
        }

        public boolean intersects(Bounds other) {
            return !(other.raMax < raMin || other.raMin > raMax ||
                    other.decMax < decMin || other.decMin > decMax);
        }
    }

    /**
     * Create a new QuadTree node.
     */
    public QuadTree(Bounds bounds, int depth) {
        this.bounds = bounds;
        this.depth = depth;
        this.stars = new ArrayList<>();
        this.divided = false;
    }

    /**
     * Insert a star into the quadtree.
     */
    public boolean insert(Star star) {
        // Check if star is within bounds
        if (!bounds.contains(star.getRa(), star.getDec())) {
            return false;
        }

        // If we have capacity and not divided, add here
        if (stars.size() < MAX_CAPACITY || depth >= MAX_DEPTH) {
            stars.add(star);
            return true;
        }

        // Otherwise, subdivide if needed and insert into children
        if (!divided) {
            subdivide();
        }

        // Try to insert into one of the children
        for (QuadTree child : children) {
            if (child.insert(star)) {
                return true;
            }
        }

        // Fallback: add to this node even if over capacity
        stars.add(star);
        return true;
    }

    /**
     * Subdivide this node into four children.
     */
    private void subdivide() {
        double raMid = (bounds.raMin + bounds.raMax) / 2.0;
        double decMid = (bounds.decMin + bounds.decMax) / 2.0;

        children = new QuadTree[4];

        // NE quadrant
        children[0] = new QuadTree(
            new Bounds(raMid, bounds.raMax, decMid, bounds.decMax),
            depth + 1
        );

        // NW quadrant
        children[1] = new QuadTree(
            new Bounds(bounds.raMin, raMid, decMid, bounds.decMax),
            depth + 1
        );

        // SE quadrant
        children[2] = new QuadTree(
            new Bounds(raMid, bounds.raMax, bounds.decMin, decMid),
            depth + 1
        );

        // SW quadrant
        children[3] = new QuadTree(
            new Bounds(bounds.raMin, raMid, bounds.decMin, decMid),
            depth + 1
        );

        divided = true;

        // Move existing stars to children
        List<Star> existingStars = new ArrayList<>(stars);
        stars.clear();

        for (Star star : existingStars) {
            boolean inserted = false;
            for (QuadTree child : children) {
                if (child.insert(star)) {
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                stars.add(star);  // Keep in this node if can't insert to children
            }
        }
    }

    /**
     * Query stars within a region.
     */
    public List<Star> query(Bounds range) {
        List<Star> found = new ArrayList<>();

        // If range doesn't intersect bounds, return empty
        if (!bounds.intersects(range)) {
            return found;
        }

        // Check stars in this node
        for (Star star : stars) {
            if (range.contains(star.getRa(), star.getDec())) {
                found.add(star);
            }
        }

        // Recursively check children
        if (divided) {
            for (QuadTree child : children) {
                found.addAll(child.query(range));
            }
        }

        return found;
    }

    /**
     * Query stars within a circular region (cone search).
     */
    public List<Star> queryRadius(double centerRA, double centerDec, double radiusDeg) {
        // Create bounding box for the circle
        Bounds range = new Bounds(
            centerRA - radiusDeg,
            centerRA + radiusDeg,
            centerDec - radiusDeg,
            centerDec + radiusDeg
        );

        // Get candidates from rectangular region
        List<Star> candidates = query(range);

        // Filter to actual circular region
        List<Star> found = new ArrayList<>();
        double radiusRad = Math.toRadians(radiusDeg);

        for (Star star : candidates) {
            double distance = angularDistance(centerRA, centerDec, star.getRa(), star.getDec());
            if (distance <= radiusRad) {
                found.add(star);
            }
        }

        return found;
    }

    /**
     * Calculate angular distance between two points on celestial sphere.
     */
    private double angularDistance(double ra1, double dec1, double ra2, double dec2) {
        double ra1Rad = Math.toRadians(ra1 * 15.0);  // Hours to degrees to radians
        double dec1Rad = Math.toRadians(dec1);
        double ra2Rad = Math.toRadians(ra2 * 15.0);
        double dec2Rad = Math.toRadians(dec2);

        // Haversine formula
        double dRA = ra2Rad - ra1Rad;
        double dDec = dec2Rad - dec1Rad;

        double a = Math.sin(dDec / 2) * Math.sin(dDec / 2) +
                  Math.cos(dec1Rad) * Math.cos(dec2Rad) *
                  Math.sin(dRA / 2) * Math.sin(dRA / 2);

        return 2 * Math.asin(Math.sqrt(a));
    }

    /**
     * Find the nearest star to a given position.
     */
    public Star findNearest(double ra, double dec, double maxRadius) {
        List<Star> candidates = queryRadius(ra, dec, maxRadius);

        if (candidates.isEmpty()) {
            return null;
        }

        Star nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Star star : candidates) {
            double distance = angularDistance(ra, dec, star.getRa(), star.getDec());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = star;
            }
        }

        return nearest;
    }

    /**
     * Get total number of stars in tree.
     */
    public int size() {
        int count = stars.size();
        if (divided) {
            for (QuadTree child : children) {
                count += child.size();
            }
        }
        return count;
    }

    /**
     * Clear the tree.
     */
    public void clear() {
        stars.clear();
        if (divided) {
            for (QuadTree child : children) {
                child.clear();
            }
            children = null;
            divided = false;
        }
    }
}

