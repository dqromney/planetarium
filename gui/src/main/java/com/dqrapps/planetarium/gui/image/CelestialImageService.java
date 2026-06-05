package com.dqrapps.planetarium.gui.image;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Service for loading and caching celestial body images.
 * Provides realistic images for planets, moons, and the sun with proper sizing.
 */
public class CelestialImageService {

  private static final Logger log = Logger.getLogger(CelestialImageService.class.getName());
  private static CelestialImageService instance;

  private final Map<String, Image> imageCache = new HashMap<>();
  private final Set<String> missingImages = new HashSet<>(); // Track images we've already tried and failed
  private final String basePath = "/com/dqrapps/planetarium/gui/images/celestial/";

  // Default sizes for celestial bodies (in pixels) - auto-scales based on these
  private final Map<String, Double> defaultSizes = new HashMap<>();

  private CelestialImageService() {
    initializeDefaultSizes();
    preloadCommonImages();
  }

  public static CelestialImageService getInstance() {
    if (instance == null) {
      instance = new CelestialImageService();
    }
    return instance;
  }

  /**
   * Initialize default display sizes for celestial bodies.
   * These are base sizes that will be scaled according to magnitude and distance.
   */
  private void initializeDefaultSizes() {
    // Sun and Moon (large, close objects)
    defaultSizes.put("sun", 32.0);
    defaultSizes.put("moon", 24.0);

    // Inner planets (smaller, closer)
    defaultSizes.put("mercury", 8.0);
    defaultSizes.put("venus", 12.0);
    defaultSizes.put("earth", 12.0);
    defaultSizes.put("mars", 10.0);

    // Outer planets (larger, farther)
    defaultSizes.put("jupiter", 20.0);
    defaultSizes.put("saturn", 18.0);
    defaultSizes.put("uranus", 14.0);
    defaultSizes.put("neptune", 14.0);

    // Dwarf planets
    defaultSizes.put("pluto", 7.0);
  }

  /**
   * Preload commonly used images to improve performance.
   */
  private void preloadCommonImages() {
    String[] commonBodies = {"sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn"};
    for (String body : commonBodies) {
      loadImage(body);
    }
  }

  /**
   * Load an image for a celestial body.
   * Falls back to generating a simple circle if image not found.
   * Supports PNG, JPG, and JPEG formats.
   *
   * @param bodyName Name of the celestial body (lowercase)
   * @return Image object, or null if generation needed
   */
  public Image loadImage(String bodyName) {
    String key = bodyName.toLowerCase();

    // Check cache first
    if (imageCache.containsKey(key)) {
      return imageCache.get(key);
    }

    // Check if we've already tried and failed for this image
    if (missingImages.contains(key)) {
      return null; // Already logged, don't spam
    }

    // Try multiple formats (PNG first, then JPG, then JPEG)
    String[] extensions = {".png", ".jpg", ".jpeg"};

    for (String ext : extensions) {
      try {
        String imagePath = basePath + key + ext;
        InputStream is = getClass().getResourceAsStream(imagePath);

        if (is != null) {
          Image image = new Image(is, 0, 0, true, true); // Preserve aspect ratio, smooth scaling
          imageCache.put(key, image);
          log.info("Loaded image for " + bodyName + ": " + imagePath);
          return image;
        }
      } catch (Exception e) {
        // Try next format
      }
    }

    // Mark as missing and log once
    missingImages.add(key);
    log.info("Image not found for " + bodyName + " - using fallback rendering");
    return null;
  }

  /**
   * Get the default display size for a celestial body.
   *
   * @param bodyName Name of the celestial body
   * @return Default size in pixels
   */
  public double getDefaultSize(String bodyName) {
    return defaultSizes.getOrDefault(bodyName.toLowerCase(), 10.0);
  }

  /**
   * Calculate auto-sized dimensions for a celestial body based on zoom and distance.
   *
   * @param bodyName Name of the body
   * @param baseMagnification Base magnification factor (1.0 = normal)
   * @param distanceAU Distance in AU (optional, for size scaling)
   * @return Calculated size in pixels
   */
  public double calculateAutoSize(String bodyName, double baseMagnification, double distanceAU) {
    double baseSize = getDefaultSize(bodyName);

    // Apply magnification
    double size = baseSize * baseMagnification;

    // Optionally scale by distance (closer = larger)
    if (distanceAU > 0) {
      // Logarithmic scaling to keep sizes reasonable
      double distanceFactor = Math.max(0.5, Math.min(2.0, 1.0 / Math.log10(distanceAU + 1)));
      size *= distanceFactor;
    }

    // Clamp to reasonable bounds
    return Math.max(6.0, Math.min(size, 64.0));
  }

  /**
   * Clear the image cache (useful for memory management or when adding new images).
   */
  public void clearCache() {
    imageCache.clear();
    missingImages.clear();
    log.info("Cleared celestial image cache");
  }

  /**
   * Get cache statistics for debugging.
   */
  public String getCacheStats() {
    return String.format("Cached images: %d, Missing (fallback): %d", imageCache.size(), missingImages.size());
  }

  /**
   * Get list of bodies using fallback rendering.
   */
  public Set<String> getMissingImages() {
    return new HashSet<>(missingImages);
  }
}
