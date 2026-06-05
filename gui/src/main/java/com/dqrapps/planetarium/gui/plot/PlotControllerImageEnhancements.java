package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.image.CelestialImageService;
import com.dqrapps.planetarium.logic.model.MoonPosition;
import com.dqrapps.planetarium.logic.model.Planet;
import com.dqrapps.planetarium.logic.model.SunPosition;
import com.dqrapps.planetarium.logic.service.SkyProjection;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Enhancement methods for PlotController to add realistic image rendering.
 * These methods replace the simple colored circles with actual NASA/scientific images.
 *
 * To integrate: Copy these methods into PlotController.java
 */
public class PlotControllerImageEnhancements {

  private final CelestialImageService imageService = CelestialImageService.getInstance();

  /**
   * Draw the Sun with realistic image (enhanced version).
   * Falls back to original rendering if image not available.
   */
  private void drawSunWithImage(GraphicsContext gc, SunPosition currentSunPosition,
                                SkyProjection projection, boolean showSun) {
    if (!showSun || currentSunPosition == null || projection == null) {
      return;
    }

    if (currentSunPosition.isVisible()) {
      double[] coords = projection.raDecToScreen(currentSunPosition.getRa(), currentSunPosition.getDec());
      if (coords != null) {
        double x = coords[0];
        double y = coords[1];

        // Auto-size based on zoom/distance
        double size = imageService.calculateAutoSize("sun", 1.0, 1.0);

        // Check if on screen
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        if (x >= -size && x <= width + size && y >= -size && y <= height + size) {

          // Try to load sun image
          Image sunImage = imageService.loadImage("sun");

          if (sunImage != null) {
            // Draw with image
            // Draw glow effect first
            gc.setFill(Color.rgb(255, 255, 150, 0.3));
            gc.fillOval(x - size * 1.5, y - size * 1.5, size * 3, size * 3);

            // Draw sun image with circular clipping
            gc.save();
            gc.beginPath();
            gc.arc(x, y, size/2, size/2, 0, 360);
            gc.closePath();
            gc.clip();
            gc.drawImage(sunImage, x - size/2, y - size/2, size, size);
            gc.restore();

            // Add subtle corona glow on top
            gc.setFill(Color.rgb(255, 245, 200, 0.2));
            gc.fillOval(x - size * 0.8, y - size * 0.8, size * 1.6, size * 1.6);

          } else {
            // Fallback to original circle rendering
            drawSunFallback(gc, x, y, size);
          }

          // Label
          gc.setFill(Color.rgb(255, 255, 150));
          gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 12));
          gc.fillText("☀ Sun", x + size/2 + 5, y - size/2);
        }
      }
    } else {
      // Sun below horizon - show indicator
      drawHorizonIndicator(gc, "☀ Sun (below horizon)",
                          Color.rgb(255, 200, 100),
                          currentSunPosition.getAltitude());
    }
  }

  /**
   * Fallback sun rendering (original style).
   */
  private void drawSunFallback(GraphicsContext gc, double x, double y, double size) {
    // Draw bright glow effect
    gc.setFill(Color.rgb(255, 255, 150, 0.4));
    gc.fillOval(x - size * 1.5, y - size * 1.5, size * 3, size * 3);

    // Draw sun disk
    gc.setFill(Color.rgb(255, 255, 100));
    gc.fillOval(x - size/2, y - size/2, size, size);

    // Add corona effect
    gc.setFill(Color.rgb(255, 245, 200, 0.6));
    gc.fillOval(x - size/3, y - size/3, size/1.5, size/1.5);
  }

  /**
   * Draw the Moon with realistic image showing current phase (enhanced version).
   * Falls back to original rendering if image not available.
   */
  private void drawMoonWithImage(GraphicsContext gc, MoonPosition currentMoonPosition,
                                  SkyProjection projection, boolean showMoon) {
    if (!showMoon || currentMoonPosition == null || projection == null) {
      return;
    }

    if (currentMoonPosition.isVisible()) {
      double[] coords = projection.raDecToScreen(currentMoonPosition.getRa(), currentMoonPosition.getDec());
      if (coords != null) {
        double x = coords[0];
        double y = coords[1];

        // Auto-size based on zoom/distance
        double size = imageService.calculateAutoSize("moon", 1.0, 0.00257); // Moon distance ~0.00257 AU

        double illumination = currentMoonPosition.getIllumination();

        // Check if on screen
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        if (x >= -size && x <= width + size && y >= -size && y <= height + size) {

          // Try to load moon image
          Image moonImage = imageService.loadImage("moon");

          if (moonImage != null) {
            // Draw with image
            // Subtle glow first
            gc.setFill(Color.rgb(200, 200, 220, 0.2));
            gc.fillOval(x - size * 0.9, y - size * 0.9, size * 1.8, size * 1.8);

            // Draw moon image with circular clipping
            gc.save();
            gc.beginPath();
            gc.arc(x, y, size/2, size/2, 0, 360);
            gc.closePath();
            gc.clip();
            gc.drawImage(moonImage, x - size/2, y - size/2, size, size);

            // Draw phase shadow overlay (still clipped to circle)
            if (illumination < 0.98) {
              gc.setFill(Color.rgb(10, 10, 35, 0.7)); // Dark space color for shadow

              if (illumination < 0.5) {
                // Waning (shadow on right)
                double shadowWidth = size * (1 - illumination * 2);
                gc.fillOval(x - size/2 + size - shadowWidth, y - size/2, shadowWidth, size);
              } else {
                // Waxing (shadow on left)
                double shadowWidth = size * (2 - illumination * 2);
                gc.fillOval(x - size/2, y - size/2, shadowWidth, size);
              }
            }
            gc.restore();

          } else {
            // Fallback to original circle rendering
            drawMoonFallback(gc, x, y, size, illumination);
          }

          // Label with phase information
          gc.setFill(Color.rgb(200, 200, 220));
          gc.setFont(javafx.scene.text.Font.font("Arial", 10));
          String label = String.format("%s %s (%d%%)",
              currentMoonPosition.getPhaseEmoji(),
              currentMoonPosition.getPhaseName(),
              currentMoonPosition.getIlluminationPercent());
          gc.fillText(label, x + size/2 + 5, y - size/2);
        }
      }
    } else {
      // Moon below horizon
      drawHorizonIndicator(gc,
          String.format("🌙 Moon (%s, %d%% lit) - below horizon",
              currentMoonPosition.getPhaseName(),
              currentMoonPosition.getIlluminationPercent()),
          Color.rgb(180, 180, 200),
          currentMoonPosition.getAltitude());
    }
  }

  /**
   * Fallback moon rendering (original style).
   */
  private void drawMoonFallback(GraphicsContext gc, double x, double y, double size, double illumination) {
    // Draw moon disk (light gray)
    gc.setFill(Color.rgb(220, 220, 220, 0.9));
    gc.fillOval(x - size/2, y - size/2, size, size);

    // Draw shadow for current phase
    if (illumination < 0.98) {
      gc.setFill(Color.rgb(50, 50, 80, 0.8));

      if (illumination < 0.5) {
        // Crescent - shadow on right side
        double shadowWidth = size * (1 - illumination * 2);
        gc.fillOval(x - size/2 + size - shadowWidth, y - size/2, shadowWidth, size);
      } else {
        // Gibbous - shadow on left side
        double shadowWidth = size * (2 - illumination * 2);
        gc.fillOval(x - size/2, y - size/2, shadowWidth, size);
      }
    }

    // Add subtle glow
    gc.setFill(Color.rgb(200, 200, 220, 0.3));
    gc.fillOval(x - size * 0.8, y - size * 0.8, size * 1.6, size * 1.6);
  }

  /**
   * Draw planet with realistic image (enhanced version).
   * Falls back to original rendering if image not available.
   */
  private void drawPlanetWithImage(GraphicsContext gc, Planet planet, double x, double y) {
    // Calculate size based on planet distance and base size
    double baseMagnification = 1.0;
    if (planet.isHovered()) {
      baseMagnification = 1.3; // Enlarge hovered planets
    }

    double distanceAU = planet.getDistance(); // Distance in AU
    double size = imageService.calculateAutoSize(planet.getName().toLowerCase(),
                                                  baseMagnification, distanceAU);

    Color color = Color.web(planet.getDisplayColor());

    // Draw glow for bright planets or when hovered
    if (planet.getMagnitude() < 1.0 || planet.isHovered()) {
      double glowOpacity = planet.isHovered() ? 0.5 : 0.3;
      gc.setFill(Color.rgb((int)(color.getRed()*255), (int)(color.getGreen()*255),
                          (int)(color.getBlue()*255), glowOpacity));
      gc.fillOval(x - size * 1.2, y - size * 1.2, size * 2.4, size * 2.4);
    }

    // Draw selection ring for hovered planets
    if (planet.isHovered()) {
      gc.setStroke(Color.rgb(255, 255, 255, 0.8));
      gc.setLineWidth(2.0);
      gc.strokeOval(x - size * 0.7, y - size * 0.7, size * 1.4, size * 1.4);
    }

    // Try to load planet image
    Image planetImage = imageService.loadImage(planet.getName().toLowerCase());

    if (planetImage != null) {
      // Draw with realistic image with circular clipping
      gc.save();
      gc.beginPath();
      gc.arc(x, y, size/2, size/2, 0, 360);
      gc.closePath();
      gc.clip();
      gc.drawImage(planetImage, x - size/2, y - size/2, size, size);
      gc.restore();

      // Still draw special features for Saturn (rings) on top of image
      if (planet.getName().equalsIgnoreCase("Saturn")) {
        drawSaturnRings(gc, x, y, size, color);
      }

    } else {
      // Fallback to original colored circle rendering
      gc.setFill(color);
      gc.fillOval(x - size/2, y - size/2, size, size);

      // Draw special features
      if (planet.getName().equalsIgnoreCase("Saturn")) {
        drawSaturnRings(gc, x, y, size, color);
      } else if (planet.getName().equalsIgnoreCase("Jupiter")) {
        drawJupiterBands(gc, x, y, size, color);
      }
    }

    // Enhanced labeling
    gc.setFill(Color.rgb(255, 255, 200));
    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.NORMAL, 10));

    String label = String.format("%s %s", planet.getSymbol(), planet.getName());

    if (planet.isDwarfPlanet()) {
      label += " (Dwarf)";
    } else if (planet.isAsteroid()) {
      label += " (Asteroid)";
    }

    gc.fillText(label, x + size/2 + 3, y - size/2 - 2);

    // Info for bright objects or when hovered
    if (planet.getMagnitude() < 8.0 || planet.isHovered()) {
      gc.setFont(javafx.scene.text.Font.font("Arial", 8));
      gc.setFill(Color.rgb(200, 200, 200, 0.8));
      String info = String.format("mag %.1f, %s", planet.getMagnitude(), planet.getDistanceString());
      gc.fillText(info, x + size/2 + 3, y + size/2 + 10);

      if (planet.isHovered() && planet.getOrbitalPeriod() > 0) {
        String periodInfo = String.format("Period: %s", planet.getOrbitalPeriodString());
        gc.fillText(periodInfo, x + size/2 + 3, y + size/2 + 22);
      }
    }
  }

  /**
   * Draw Saturn's rings (kept from original for compatibility).
   */
  private void drawSaturnRings(GraphicsContext gc, double x, double y, double planetSize, Color planetColor) {
    // Outer ring
    gc.setStroke(Color.rgb(200, 180, 120, 0.7)); // Slightly more opaque for visibility
    gc.setLineWidth(2.0); // Thicker for better visibility
    double ringSize = planetSize * 1.9;
    gc.strokeOval(x - ringSize/2, y - ringSize/2, ringSize, ringSize * 0.25);

    // Inner ring
    gc.setLineWidth(1.5);
    double innerRingSize = planetSize * 1.5;
    gc.strokeOval(x - innerRingSize/2, y - innerRingSize/2, innerRingSize, innerRingSize * 0.25);

    // Cassini division (gap between rings)
    gc.setStroke(Color.rgb(10, 10, 35, 0.5)); // Dark gap
    gc.setLineWidth(0.5);
    double gapSize = planetSize * 1.7;
    gc.strokeOval(x - gapSize/2, y - gapSize/2, gapSize, gapSize * 0.25);
  }

  /**
   * Draw Jupiter's bands (kept from original for compatibility).
   */
  private void drawJupiterBands(GraphicsContext gc, double x, double y, double planetSize, Color planetColor) {
    gc.setStroke(Color.rgb(139, 100, 20, 0.5));
    gc.setLineWidth(0.8);

    double bandSpacing = planetSize / 5;
    for (int i = -2; i <= 2; i++) {
      double bandY = y + i * bandSpacing;
      gc.strokeLine(x - planetSize/2, bandY, x + planetSize/2, bandY);
    }
  }

  /**
   * Draw horizon indicator (kept from original).
   */
  private void drawHorizonIndicator(GraphicsContext gc, String text, Color color, double altitude) {
    double width = gc.getCanvas().getWidth();
    double height = gc.getCanvas().getHeight();

    double y = height - 25;
    double x = 20;

    gc.setFill(Color.rgb(0, 0, 0, 0.7));
    gc.fillRect(x - 5, y - 15, Math.min(text.length() * 7 + 10, width - 30), 20);

    gc.setFill(color);
    gc.setFont(javafx.scene.text.Font.font("Arial", 10));
    gc.fillText(text, x, y);

    if (altitude < 0) {
      String altText = String.format("(%.1f° below horizon)", Math.abs(altitude));
      gc.setFill(Color.rgb(180, 180, 180));
      gc.fillText(altText, x, y + 12);
    }
  }
}
