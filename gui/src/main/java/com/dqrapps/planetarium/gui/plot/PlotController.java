package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.gui.config.DisplayPreferences;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Constellation;
import com.dqrapps.planetarium.logic.model.ConstellationLine;
import com.dqrapps.planetarium.logic.model.Constellations;
import com.dqrapps.planetarium.logic.model.DeepSkyObject;
import com.dqrapps.planetarium.logic.model.MoonPosition;
import com.dqrapps.planetarium.logic.model.Planet;
import com.dqrapps.planetarium.logic.model.Star;
import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.model.SunPosition;
import com.dqrapps.planetarium.logic.service.AstroService;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.service.ConstellationService;
import com.dqrapps.planetarium.logic.service.MoonCalculator;
import com.dqrapps.planetarium.logic.service.PlanetService;
import com.dqrapps.planetarium.logic.service.SkyProjection;
import com.dqrapps.planetarium.logic.service.StarService;
import com.dqrapps.planetarium.logic.service.SunCalculator;
import com.dqrapps.planetarium.logic.type.SkyViewMode;
import com.dqrapps.planetarium.logic.type.StarCatalog;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlotController {

    // Logger for the controller
    private static final Logger log = Logger.getLogger(PlotController.class.getName());

    @FXML
    private Canvas starCanvas;

    @FXML
    private Pane canvasPane;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField searchField;

    @FXML
    private javafx.scene.control.Slider speedSlider;

    @FXML
    private javafx.scene.control.Label speedLabel;

    @FXML
    private javafx.scene.control.Button playButton;

    // Menu buttons for organized toolbar
    @FXML
    private javafx.scene.control.MenuButton objectsMenuButton;

    @FXML
    private javafx.scene.control.MenuButton displayMenuButton;

    // Menu items for celestial objects
    @FXML
    private javafx.scene.control.CheckMenuItem sunMenuItem;

    @FXML
    private javafx.scene.control.CheckMenuItem moonMenuItem;

    @FXML
    private javafx.scene.control.CheckMenuItem planetsMenuItem;

    @FXML
    private javafx.scene.control.CheckMenuItem constellationsMenuItem;

    // Menu items for display options
    @FXML
    private javafx.scene.control.CheckMenuItem gridMenuItem;

    @FXML
    private javafx.scene.control.CheckMenuItem dsoMenuItem;

    @FXML
    private javafx.scene.control.CheckMenuItem orbitalPathsMenuItem;

    // Menu items for view modes (hemisphere display)
    @FXML
    private javafx.scene.control.MenuButton viewMenuButton;

    @FXML
    private javafx.scene.control.RadioMenuItem singleHemisphereMenuItem;

    @FXML
    private javafx.scene.control.RadioMenuItem dualHemisphereMenuItem;

    @FXML
    private javafx.scene.control.RadioMenuItem fullSkyMenuItem;

    private GraphicsContext gc;
    private ConfigService configService;
    private StarService starService;
    private AstroService astroService;
    private ConstellationService constellationService;
    private PlanetService planetService;
    private Config config;
    private Stars stars;
    private Constellations constellations;
    private List<DeepSkyObject> deepSkyObjects;
    private List<Planet> planets;
    private SkyProjection projection;

    // Display preferences (centralized in singleton)
    private DisplayPreferences displayPrefs;

    // Enhanced catalog management (Multiple HYG catalogs)
    private StarCatalog currentCatalog = StarCatalog.HYG_5000;  // Default: 5K stars for good balance
    private int currentStarCount = 0;

    // Performance optimization fields (Phase 2 & 3)
    private AnimationTimer renderTimer;
    private ExecutorService calculationExecutor;
    private long lastFrameTime = 0;
    private static final long FRAME_INTERVAL = 16_666_667L; // 60 FPS (nanoseconds)
    private boolean needsRecalculation = true;
    private List<Star> visibleStarsCache;
    private int frameCount = 0;
    private long fpsStartTime = 0;
    private double currentFPS = 0;

    // Interaction smoothing (Phase 5 fix)
    private long lastInteractionTime = 0;
    private static final long INTERACTION_DEBOUNCE = 100_000L; // 10ms debounce

    // Pan and zoom fields (Phase 3)
    private double viewCenterRA = 12.0;      // Center RA (hours)
    private double viewCenterDec = 40.0;     // Center Dec (degrees)
    private double fieldOfView = 120.0;      // FOV in degrees
    private double zoomLevel = 1.0;          // Zoom factor (1.0 = normal)

    // Mouse interaction fields (Phase 3)
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private boolean isDragging = false;
    private Star hoveredStar = null;

    // Time animation fields (Phase 5)
    private boolean timeAnimationRunning = false;
    private double timeAnimationSpeed = 60.0; // 60x realtime = 1 hour per second
    private LocalDateTime animationTime;
    private LocalDateTime originalTime;
    private double accumulatedSeconds = 0.0; // Accumulate fractional seconds for smooth animation
    private LocalDateTime lastPlanetUpdate = null; // Track when planets were last updated

    // Sun and Moon position fields (Phase 5 Enhancement)
    private SunPosition currentSunPosition;
    private MoonPosition currentMoonPosition;
    private boolean showSun = true;
    private boolean showMoon = true;
    private LocalDateTime lastSunMoonUpdate = null; // Cache to avoid recalculating every frame

    // Star search fields (Phase 5)
    private Star highlightedStar = null;

    // Planet hover and orbital path fields (Phase Enhancement)
    private Planet hoveredPlanet = null;
    private boolean showOrbitalPaths = false;
    private java.util.Map<String, List<Planet.OrbitalPoint>> orbitalPathCache = new java.util.HashMap<>();
    private LocalDateTime lastOrbitalPathUpdate = null;
    private static final long ORBITAL_PATH_UPDATE_INTERVAL = 24 * 60 * 60 * 1000L; // Update daily
    private javafx.scene.control.Tooltip planetTooltip;

    // Hemisphere view mode fields (Dual Hemisphere Enhancement)
    private SkyViewMode currentViewMode = SkyViewMode.SINGLE_HEMISPHERE;

    @SneakyThrows
    @FXML
    private void initialize() {
        configService = ConfigService.getInstance();
        config = configService.getCurrentConfig();
        starService = StarService.getInstance();
        constellationService = ConstellationService.getInstance();
        planetService = PlanetService.getInstance();
        astroService = new AstroService(null);
        displayPrefs = DisplayPreferences.getInstance();

        // Initialize view center from config
        if (config != null && config.getLatitudeDegrees() != null) {
            try {
                viewCenterDec = Double.parseDouble(config.getLatitudeDegrees());
            } catch (NumberFormatException e) {
                viewCenterDec = 40.0; // Default
            }
        }

        // Initialize background calculation executor
        calculationExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "StarCalculationThread");
            t.setDaemon(true);
            return t;
        });

        if (starService.defaultStarsExists()) {
            stars = starService.getStars();
            if (stars != null && stars.getStarList() != null) {
                currentStarCount = stars.getStarList().size();
                log.info("Loaded catalog with " + currentStarCount + " stars");
            }
        }

        // Load constellation data
        if (constellationService.getConstellations() != null) {
            constellations = constellationService.getConstellations();
            log.info("Loaded " +
                (constellations.getConstellations() != null ? constellations.getConstellations().size() : 0) +
                " constellations");
        }

        // Load deep sky objects (Phase 8)
        loadDeepSkyObjects();

        // Calculate planet positions (Phase 8)
        updatePlanetPositions();

        // Initialize canvas
        if (starCanvas != null) {
            gc = starCanvas.getGraphicsContext2D();

            // Make canvas responsive to pane size
            if (canvasPane != null) {
                starCanvas.widthProperty().bind(canvasPane.widthProperty());
                starCanvas.heightProperty().bind(canvasPane.heightProperty());

                // Mark for recalculation when canvas size changes
                starCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
                    needsRecalculation = true;
                });
                starCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
                    needsRecalculation = true;
                });
            }

            // Setup mouse interaction (Phase 3)
            setupMouseInteraction();

            // Setup speed slider (Phase 9)
            setupSpeedSlider();

            // Setup search field listener (Phase 5)
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                    // Clear highlight when search field is cleared
                    if (newVal == null || newVal.trim().isEmpty()) {
                        highlightedStar = null;
                    }
                });
            }

            // Synchronize menu items with current state
            synchronizeMenuItems();

            // Start the rendering loop
            Platform.runLater(this::startRenderLoop);
        }
    }

    /**
     * Synchronize menu item states with current application settings.
     */
    private void synchronizeMenuItems() {
        if (sunMenuItem != null) {
            sunMenuItem.setSelected(showSun);
        }
        if (moonMenuItem != null) {
            moonMenuItem.setSelected(showMoon);
        }
        if (planetsMenuItem != null) {
            planetsMenuItem.setSelected(displayPrefs.isShowPlanets());
        }
        if (constellationsMenuItem != null) {
            constellationsMenuItem.setSelected(displayPrefs.isShowConstellations());
        }
        if (gridMenuItem != null) {
            gridMenuItem.setSelected(displayPrefs.isShowGrid());
        }
        if (dsoMenuItem != null) {
            dsoMenuItem.setSelected(displayPrefs.isShowDSO());
        }
        if (orbitalPathsMenuItem != null) {
            orbitalPathsMenuItem.setSelected(showOrbitalPaths);
        }

        // Synchronize view mode menu items
        if (singleHemisphereMenuItem != null) {
            singleHemisphereMenuItem.setSelected(currentViewMode == SkyViewMode.SINGLE_HEMISPHERE);
        }
        if (dualHemisphereMenuItem != null) {
            dualHemisphereMenuItem.setSelected(currentViewMode == SkyViewMode.DUAL_HEMISPHERE);
        }
        if (fullSkyMenuItem != null) {
            fullSkyMenuItem.setSelected(currentViewMode == SkyViewMode.FULL_SKY_MERCATOR);
        }
    }

    /**
     * Setup mouse event handlers for pan, zoom, and hover (Phase 3).
     */
    private void setupMouseInteraction() {
        // Mouse drag for panning
        starCanvas.setOnMousePressed(e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            isDragging = true;
        });

        starCanvas.setOnMouseDragged(e -> {
            if (isDragging) {
                double deltaX = e.getX() - lastMouseX;
                double deltaY = e.getY() - lastMouseY;

                // Pan the view
                panView(deltaX, deltaY);

                // Clear search highlight when user manually pans (Phase 5 fix)
                highlightedStar = null;

                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        starCanvas.setOnMouseReleased(e -> {
            isDragging = false;
        });

        // Mouse scroll for zooming
        starCanvas.setOnScroll(e -> {
            double zoomFactor = e.getDeltaY() > 0 ? 1.1 : 0.9;
            zoom(zoomFactor, e.getX(), e.getY());
        });

        // Mouse move for star hover detection
        starCanvas.setOnMouseMoved(e -> {
            updateHoveredStar(e.getX(), e.getY());
        });
    }

    /**
     * Pan the view based on mouse drag (Phase 3, Phase 5 fix).
     */
    private void panView(double deltaX, double deltaY) {
        // Convert pixel delta to sky coordinates
        double scale = fieldOfView / Math.min(starCanvas.getWidth(), starCanvas.getHeight());

        // Pan RA (horizontal)
        viewCenterRA -= (deltaX * scale) / 15.0;  // Convert degrees to hours

        // Wrap RA to 0-24 range
        while (viewCenterRA < 0) viewCenterRA += 24.0;
        while (viewCenterRA >= 24.0) viewCenterRA -= 24.0;

        // Pan Dec (vertical)
        viewCenterDec += deltaY * scale;

        // Clamp Dec to -90 to +90
        viewCenterDec = Math.max(-90, Math.min(90, viewCenterDec));

        // Trigger recalculation (debounced)
        lastInteractionTime = System.nanoTime();
        needsRecalculation = true;
    }

    /**
     * Zoom the view (Phase 3, Phase 5 fix).
     */
    private void zoom(double factor, double mouseX, double mouseY) {
        // Adjust zoom level
        zoomLevel *= factor;
        zoomLevel = Math.max(0.1, Math.min(10.0, zoomLevel));  // Clamp 0.1x to 10x

        // Adjust field of view
        fieldOfView /= factor;
        fieldOfView = Math.max(10.0, Math.min(170.0, fieldOfView));  // Clamp 10° to 170°

        // Trigger recalculation (debounced)
        lastInteractionTime = System.nanoTime();
        needsRecalculation = true;
    }

    /**
     * Update hovered star and planet based on mouse position (Phase 3, Enhanced).
     */
    private void updateHoveredStar(double mouseX, double mouseY) {
        // Clear previous hover states
        hoveredStar = null;
        if (hoveredPlanet != null) {
            hoveredPlanet.setHovered(false);
            hoveredPlanet = null;
        }

        // Check for planet hover first (higher priority)
        if (planets != null && displayPrefs.isShowPlanets()) {
            double planetThreshold = 20.0; // Larger threshold for planets
            for (Planet planet : planets) {
                if (planet.isNearPoint(mouseX, mouseY, planetThreshold)) {
                    hoveredPlanet = planet;
                    planet.setHovered(true);
                    showPlanetTooltip(planet, mouseX, mouseY);
                    return; // Exit early if planet is hovered
                }
            }
        }

        // Check for star hover if no planet is hovered
        if (visibleStarsCache == null || visibleStarsCache.isEmpty()) {
            hidePlanetTooltip();
            return;
        }

        // Find closest star to mouse position
        Star closest = null;
        double minDist = 15.0;  // Max 15 pixels away

        for (Star star : visibleStarsCache) {
            if (star.isPositionCached()) {
                double dx = star.getScreenX() - mouseX;
                double dy = star.getScreenY() - mouseY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < minDist) {
                    minDist = dist;
                    closest = star;
                }
            }
        }

        hoveredStar = closest;
        hidePlanetTooltip(); // Hide planet tooltip when hovering over stars
    }

    /**
     * Show planet tooltip at mouse position
     */
    private void showPlanetTooltip(Planet planet, double mouseX, double mouseY) {
        if (planetTooltip == null) {
            planetTooltip = new javafx.scene.control.Tooltip();
            planetTooltip.setShowDelay(javafx.util.Duration.millis(100));
            planetTooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #2b2b2b; -fx-text-fill: white;");
        }

        planetTooltip.setText(planet.getHoverTooltip());

        // Install tooltip on the canvas
        if (planetTooltip.getOwnerWindow() == null) {
            javafx.scene.control.Tooltip.install(starCanvas, planetTooltip);
        }
    }

    /**
     * Hide planet tooltip
     */
    private void hidePlanetTooltip() {
        if (planetTooltip != null) {
            javafx.scene.control.Tooltip.uninstall(starCanvas, planetTooltip);
            planetTooltip = null;
        }
    }

    /**
     * Start the animation timer for smooth 60 FPS rendering (Phase 2).
     */
    private void startRenderLoop() {
        fpsStartTime = System.nanoTime();

        renderTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Frame rate control - only render at 60 FPS
                if (now - lastFrameTime >= FRAME_INTERVAL) {
                    renderFrame();
                    lastFrameTime = now;

                    // Calculate FPS
                    frameCount++;
                    if (frameCount >= 60) {
                        long elapsed = now - fpsStartTime;
                        currentFPS = frameCount / (elapsed / 1_000_000_000.0);
                        frameCount = 0;
                        fpsStartTime = now;
                    }
                }
            }
        };

        renderTimer.start();
        log.info("Rendering loop started");
    }

    /**
     * Stop the rendering loop (cleanup) (Phase 2).
     */
    private void stopRenderLoop() {
        if (renderTimer != null) {
            renderTimer.stop();
        }
        if (calculationExecutor != null) {
            calculationExecutor.shutdown();
        }
    }

    /**
     * Render a single frame (Phase 2, Phase 5 fix).
     */
    private void renderFrame() {
        long now = System.nanoTime();

        // Update time animation if running (Phase 5)
        if (timeAnimationRunning) {
            long deltaTimeNanos = now - lastFrameTime;
            double deltaSeconds = deltaTimeNanos / 1_000_000_000.0;
            updateAnimationTime(deltaSeconds);
        }

        // Only recalculate if needed and debounce period has passed (Phase 5 fix)
        if (needsRecalculation) {
            // If user is actively interacting (pan/zoom), wait for them to finish
            boolean isInteracting = (now - lastInteractionTime) < INTERACTION_DEBOUNCE;

            // Always recalculate immediately if time animation is running
            // Otherwise wait for interaction to finish
            if (timeAnimationRunning || !isInteracting) {
                calculateVisibleStars();
                needsRecalculation = false;
            }
        }

        renderStarfield();
    }

    /**
     * Update animation time based on elapsed time (Phase 5, enhanced in Phase 8, fixed in Phase 9).
     */
    private void updateAnimationTime(double deltaSeconds) {
        if (animationTime == null) {
            animationTime = LocalDateTime.now();
            originalTime = animationTime;
        }

        // Accumulate fractional seconds for smooth animation
        accumulatedSeconds += deltaSeconds * timeAnimationSpeed;

        // Only update time when we have at least 1 second accumulated
        if (accumulatedSeconds >= 1.0) {
            long secondsToAdd = (long) accumulatedSeconds;
            animationTime = animationTime.plusSeconds(secondsToAdd);
            accumulatedSeconds -= secondsToAdd; // Keep the fractional remainder

            // Update planet positions periodically (every 10 minutes of simulated time for visible movement)
            if (lastPlanetUpdate == null ||
                java.time.Duration.between(lastPlanetUpdate, animationTime).abs().getSeconds() >= 600) {
                updatePlanetPositions();
                lastPlanetUpdate = animationTime;
            }

            // Trigger recalculation for new time
            needsRecalculation = true;
        }
    }

    /**
     * Calculate visible stars on background thread for performance (Phase 2 & 3).
     * Uses spatial indexing for fast queries.
     */
    private void calculateVisibleStars() {
        if (stars == null || stars.getStarList() == null) {
            return;
        }

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        if (width == 0 || height == 0) {
            return;
        }

        // Do expensive calculations on background thread
        calculationExecutor.submit(() -> {
            try {
                // Parse configuration
                double latitude = parseLatitude();
                double lst = parseSiderealTime();

                // Use current view center (from pan/zoom)
                double centerRA = viewCenterRA;
                double centerDec = viewCenterDec;
                double fov = fieldOfView;

                SkyProjection skyProj = new SkyProjection(centerRA, centerDec, fov, width, height);
                skyProj.setLatitude(latitude);
                skyProj.setLocalSiderealTime(lst);
                skyProj.setViewMode(currentViewMode); // Set current hemisphere view mode

                // Calculate query region for spatial index
                double raRadius = (fov / 2.0) / 15.0;  // Convert degrees to hours
                double decRadius = fov / 2.0;

                // Query spatial index for stars in visible region
                List<Star> candidateStars = starService.getStarsInRadius(
                    centerRA, centerDec, Math.max(raRadius, decRadius)
                );

                // Filter and cache visible stars
                List<Star> visible = candidateStars.stream()
                    .filter(star -> star.hasValidCoordinates())
                    .filter(star -> {
                        // Check visibility
                        boolean isVisible = astroService.isVisible(star.getRa(), star.getDec(), lst, latitude);
                        if (!isVisible) return false;

                        // Calculate screen position
                        double[] coords = skyProj.raDecToScreen(star.getRa(), star.getDec());
                        if (coords == null) return false;

                        // Cache the position
                        star.cachePosition(coords[0], coords[1], true);
                        return true;
                    })
                    .sorted(Comparator.comparingDouble(Star::getMag)) // Sort by brightness
                    .limit((int)(2000 * zoomLevel)) // Dynamic LOD based on zoom
                    .collect(Collectors.toList());

                // Update cache on JavaFX thread
                Platform.runLater(() -> {
                    visibleStarsCache = visible;
                    projection = skyProj;
                });

            } catch (Exception e) {
                log.warning("Error calculating visible stars: " + e.getMessage());
            }
        });
    }

    /**
     * Render the starfield on the canvas (Phase 1, optimized in Phase 2).
     */
    private void renderStarfield() {
        if (gc == null || visibleStarsCache == null) {
            return;
        }

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        if (width == 0 || height == 0) {
            return;
        }

        // Clear canvas with dark blue background
        gc.setFill(Color.rgb(0, 0, 35));
        gc.fillRect(0, 0, width, height);

        // Draw constellation lines first (behind stars) (Phase 4)
        drawConstellationLines();

        // Draw coordinate grid (Phase 6 - optional)
        drawCoordinateGrid();

        // Draw deep sky objects (Phase 8)
        drawDeepSkyObjects();

        // Draw Sun and Moon positions (Phase 5 Enhancement)
        updateSunMoonPositions();
        drawSun();
        drawMoon();

        // Render cached visible stars
        for (Star star : visibleStarsCache) {
            if (star.isPositionCached()) {
                drawStar(star, star.getScreenX(), star.getScreenY());

                // Draw labels for bright stars (Phase 3)
                if (star.getMag() < 1.5 && star.getName() != null && !star.getName().isEmpty()) {
                    drawStarLabel(star, star.getScreenX(), star.getScreenY());
                }

                // Highlight searched star (Phase 5)
                if (highlightedStar != null && star.getName() != null &&
                    star.getName().equalsIgnoreCase(highlightedStar.getName())) {
                    drawSearchHighlight(star.getScreenX(), star.getScreenY());
                }
            }
        }

        // Draw planets (Phase 8) - on top of stars
        drawPlanets();

        // Draw hover tooltip (Phase 3)
        if (hoveredStar != null && hoveredStar.isPositionCached()) {
            drawHoverTooltip(hoveredStar, hoveredStar.getScreenX(), hoveredStar.getScreenY());
        }

        // Draw info overlay
        double latitude = parseLatitude();
        double lst = parseSiderealTime();
        drawInfo(latitude, lst, visibleStarsCache.size());
    }

    /**
     * Draw constellation lines connecting stars (Phase 4, enhanced).
     */
    private void drawConstellationLines() {
        if (!displayPrefs.isShowConstellations() || constellations == null ||
            constellations.getConstellations() == null || visibleStarsCache == null) {
            return;
        }

        // Calculate zoom-dependent opacity and line width
        // Dim when zoomed out (large FOV), brighten when zoomed in (small FOV)
        double opacity = calculateConstellationOpacity();
        double lineWidth = calculateConstellationLineWidth();

        gc.setStroke(Color.rgb(100, 130, 180, opacity));
        gc.setLineWidth(lineWidth);

        // Track constellation centers for labels
        java.util.Map<String, double[]> constellationCenters = new java.util.HashMap<>();
        java.util.Map<String, Integer> constellationLineCounts = new java.util.HashMap<>();

        for (Constellation constellation : constellations.getConstellations()) {
            if (constellation.getLines() == null) continue;

            double sumX = 0, sumY = 0;
            int validLines = 0;

            for (ConstellationLine line : constellation.getLines()) {
                // Find stars by name
                Star star1 = findStarByName(line.getStar1Name());
                Star star2 = findStarByName(line.getStar2Name());

                // Draw line if both stars are visible and cached
                if (star1 != null && star2 != null &&
                    star1.isPositionCached() && star2.isPositionCached()) {
                    gc.strokeLine(
                        star1.getScreenX(), star1.getScreenY(),
                        star2.getScreenX(), star2.getScreenY()
                    );

                    // Accumulate positions for center calculation
                    sumX += (star1.getScreenX() + star2.getScreenX()) / 2.0;
                    sumY += (star1.getScreenY() + star2.getScreenY()) / 2.0;
                    validLines++;
                }
            }

            // Calculate constellation center
            if (validLines > 0) {
                double centerX = sumX / validLines;
                double centerY = sumY / validLines;
                constellationCenters.put(constellation.getName(), new double[]{centerX, centerY});
                constellationLineCounts.put(constellation.getName(), validLines);
            }
        }

        // Draw constellation labels at centers
        drawConstellationLabels(constellationCenters, constellationLineCounts, opacity);
    }

    /**
     * Calculate constellation line opacity based on zoom level.
     * More zoomed in = brighter lines, more zoomed out = dimmer lines.
     */
    private double calculateConstellationOpacity() {
        // FOV ranges from ~10° (zoomed in) to ~170° (zoomed out)
        // Map to opacity range 0.6 (zoomed out) to 0.9 (zoomed in)
        double normalizedZoom = (170.0 - fieldOfView) / 160.0; // 0 (out) to 1 (in)
        return 0.3 + (normalizedZoom * 0.6); // Range: 0.3 to 0.9
    }

    /**
     * Calculate constellation line width based on zoom level.
     */
    private double calculateConstellationLineWidth() {
        // Thicker lines when zoomed in for better visibility
        double normalizedZoom = (170.0 - fieldOfView) / 160.0;
        return 0.8 + (normalizedZoom * 0.7); // Range: 0.8 to 1.5
    }

    /**
     * Draw constellation name labels at pattern centers.
     */
    private void drawConstellationLabels(
            java.util.Map<String, double[]> centers,
            java.util.Map<String, Integer> lineCounts,
            double opacity) {

        if (centers.isEmpty()) return;

        // Only show labels when reasonably zoomed in
        if (fieldOfView > 100) return;

        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(150, 170, 200, Math.min(opacity + 0.2, 1.0)));

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        for (java.util.Map.Entry<String, double[]> entry : centers.entrySet()) {
            String name = entry.getKey();
            double[] center = entry.getValue();
            int lineCount = lineCounts.getOrDefault(name, 0);

            // Only show labels for constellations with multiple lines visible
            if (lineCount < 2) continue;

            double x = center[0];
            double y = center[1];

            // Only draw if on screen
            if (x >= 0 && x <= width && y >= 0 && y <= height) {
                // Draw subtle background for readability
                javafx.scene.text.Text text = new javafx.scene.text.Text(name);
                text.setFont(gc.getFont());
                double textWidth = text.getLayoutBounds().getWidth();
                double textHeight = text.getLayoutBounds().getHeight();

                gc.setFill(Color.rgb(0, 0, 35, 0.7));
                gc.fillRect(x - textWidth/2 - 2, y - textHeight/2 - 2, textWidth + 4, textHeight + 4);

                // Draw label
                gc.setFill(Color.rgb(150, 170, 200, Math.min(opacity + 0.2, 1.0)));
                gc.fillText(name, x - textWidth/2, y + textHeight/4);
            }
        }
    }

    /**
     * Find a star by name in the visible stars cache (Phase 4).
     */
    private Star findStarByName(String name) {
        if (name == null || visibleStarsCache == null) return null;

        return visibleStarsCache.stream()
            .filter(s -> s.getName() != null)
            .filter(s -> s.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Draw RA/Dec coordinate grid overlay (Phase 6 - Optional).
     */
    private void drawCoordinateGrid() {
        if (!displayPrefs.isShowGrid() || projection == null) {
            return;
        }

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        if (width == 0 || height == 0) {
            return;
        }

        gc.setStroke(Color.rgb(100, 100, 100, 0.3));
        gc.setLineWidth(0.5);
        gc.setFont(javafx.scene.text.Font.font("Arial", 9));

        // Draw RA lines (vertical great circles every 2 hours)
        for (int raHour = 0; raHour < 24; raHour += 2) {
            // Draw line from south pole to north pole
            boolean firstPoint = true;
            double lastX = 0, lastY = 0;

            for (int decDeg = -90; decDeg <= 90; decDeg += 5) {
                double[] coords = projection.raDecToScreen(raHour, decDeg);

                if (coords != null) {
                    double x = coords[0];
                    double y = coords[1];

                    // Check if point is on screen
                    if (x >= 0 && x <= width && y >= 0 && y <= height) {
                        if (!firstPoint) {
                            // Draw line segment
                            gc.strokeLine(lastX, lastY, x, y);
                        }
                        lastX = x;
                        lastY = y;
                        firstPoint = false;
                    } else {
                        firstPoint = true;
                    }
                }
            }

            // Label RA hours at equator
            double[] labelPos = projection.raDecToScreen(raHour, 0);
            if (labelPos != null && labelPos[0] >= 0 && labelPos[0] <= width &&
                labelPos[1] >= 0 && labelPos[1] <= height) {
                gc.setFill(Color.rgb(150, 150, 150, 0.8));
                gc.fillText(raHour + "h", labelPos[0] + 3, labelPos[1] - 3);
            }
        }

        // Draw Dec lines (horizontal circles every 15 degrees)
        for (int decDeg = -75; decDeg <= 75; decDeg += 15) {
            boolean firstPoint = true;
            double lastX = 0, lastY = 0;

            // Draw circle around celestial sphere
            for (double raHour = 0; raHour <= 24; raHour += 0.25) {
                double[] coords = projection.raDecToScreen(raHour, decDeg);

                if (coords != null) {
                    double x = coords[0];
                    double y = coords[1];

                    // Check if point is on screen
                    if (x >= 0 && x <= width && y >= 0 && y <= height) {
                        if (!firstPoint) {
                            gc.strokeLine(lastX, lastY, x, y);
                        }
                        lastX = x;
                        lastY = y;
                        firstPoint = false;
                    } else {
                        firstPoint = true;
                    }
                }
            }

            // Label Dec degrees at RA 0h
            double[] labelPos = projection.raDecToScreen(0, decDeg);
            if (labelPos != null && labelPos[0] >= 0 && labelPos[0] <= width &&
                labelPos[1] >= 0 && labelPos[1] <= height) {
                gc.setFill(Color.rgb(150, 150, 150, 0.8));
                gc.fillText(decDeg + "°", labelPos[0] + 3, labelPos[1] + 3);
            }
        }

        // Draw celestial equator (Dec = 0°) with emphasis
        gc.setStroke(Color.rgb(150, 150, 150, 0.5));
        gc.setLineWidth(1.0);
        boolean firstPoint = true;
        double lastX = 0, lastY = 0;

        for (double raHour = 0; raHour <= 24; raHour += 0.1) {
            double[] coords = projection.raDecToScreen(raHour, 0);

            if (coords != null) {
                double x = coords[0];
                double y = coords[1];

                if (x >= 0 && x <= width && y >= 0 && y <= height) {
                    if (!firstPoint) {
                        gc.strokeLine(lastX, lastY, x, y);
                    }
                    lastX = x;
                    lastY = y;
                    firstPoint = false;
                } else {
                    firstPoint = true;
                }
            }
        }
    }

    /**
     * Draw a single star (Phase 1, Phase 4 spectral colors).
     */
    private void drawStar(Star star, double x, double y) {
        // Calculate star size based on magnitude (brighter = larger)
        double mag = star.getMag();
        double size;

        if (mag < 0) {
            size = 6.0;  // Very bright (e.g., Sirius)
        } else if (mag < 1.0) {
            size = 5.0;
        } else if (mag < 2.0) {
            size = 4.0;
        } else if (mag < 3.0) {
            size = 3.0;
        } else if (mag < 4.0) {
            size = 2.0;
        } else if (mag < 5.0) {
            size = 1.5;
        } else {
            size = 1.0;
        }

        // Adjust size with zoom
        size *= Math.sqrt(zoomLevel);

        // Calculate brightness (alpha) based on magnitude
        double brightness = Math.max(0.2, Math.min(1.0, 1.0 - (mag / 6.0)));

        // Get color (with spectral type support) (Phase 4)
        Color starColor = getStarColor(star, brightness);

        // Draw star with glow for bright stars
        if (mag < 2.0) {
            // Draw glow
            double glowSize = size * 2.5;
            gc.setFill(new Color(starColor.getRed(), starColor.getGreen(), starColor.getBlue(), 0.3));
            gc.fillOval(x - glowSize / 2, y - glowSize / 2, glowSize, glowSize);
        }

        // Draw main star
        gc.setFill(starColor);
        gc.fillOval(x - size / 2, y - size / 2, size, size);
    }

    /**
     * Get star color based on spectral type and magnitude (Phase 4).
     */
    private Color getStarColor(Star star, double brightness) {
        // If spectral type is available, use it for realistic colors
        if (star.getSpectralType() != null && !star.getSpectralType().isEmpty()) {
            char type = star.getSpectralType().charAt(0);

            switch (type) {
                case 'W': // Wolf-Rayet stars (extremely hot, >25,000 K)
                    return Color.rgb(140, 160, 255, brightness);
                case 'O': // Very hot blue stars (30,000-60,000 K)
                    return Color.rgb(155, 176, 255, brightness);
                case 'B': // Hot blue-white stars (10,000-30,000 K)
                    return Color.rgb(170, 191, 255, brightness);
                case 'A': // White stars (7,500-10,000 K)
                    return Color.rgb(202, 215, 255, brightness);
                case 'F': // Yellow-white stars (6,000-7,500 K)
                    return Color.rgb(248, 247, 255, brightness);
                case 'G': // Yellow stars like our Sun (5,000-6,000 K)
                    return Color.rgb(255, 244, 234, brightness);
                case 'K': // Orange stars (3,500-5,000 K)
                    return Color.rgb(255, 210, 161, brightness);
                case 'M': // Cool red stars (2,000-3,500 K)
                    return Color.rgb(255, 204, 111, brightness);
            }
        }

        // Fallback: use magnitude-based colors
        double mag = star.getMag();
        if (mag < 1.0) {
            return Color.rgb(200, 220, 255, brightness);
        } else if (mag < 3.0) {
            return Color.rgb(255, 255, 255, brightness);
        } else {
            return Color.rgb(255, 250, 240, brightness);
        }
    }

    /**
     * Draw label for a bright star (Phase 3).
     */
    private void drawStarLabel(Star star, double x, double y) {
        gc.setFill(Color.rgb(200, 200, 200, 0.9));
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));

        // Draw label slightly offset from star
        String label = star.getName().replaceAll("[^A-Za-z0-9 ]", ""); // Clean name
        gc.fillText(label, x + 8, y - 3);
    }

    /**
     * Draw hover tooltip for star under mouse (Phase 3).
     */
    private void drawHoverTooltip(Star star, double x, double y) {
        // Highlight the star
        gc.setStroke(Color.rgb(255, 255, 0, 0.8));
        gc.setLineWidth(2);
        gc.strokeOval(x - 10, y - 10, 20, 20);

        // Draw tooltip box
        String name = star.getName() != null ? star.getName() : "Unnamed";
        String info = String.format("%s\nRA: %.2fh  Dec: %.1f°\nMag: %.1f",
                                   name, star.getRa(), star.getDec(), star.getMag());

        String[] lines = info.split("\n");
        double boxWidth = 150;
        double boxHeight = 20 + lines.length * 15;
        double boxX = x + 15;
        double boxY = y - 10;

        // Adjust if tooltip goes off screen
        if (boxX + boxWidth > starCanvas.getWidth()) {
            boxX = x - boxWidth - 15;
        }
        if (boxY + boxHeight > starCanvas.getHeight()) {
            boxY = starCanvas.getHeight() - boxHeight - 5;
        }

        // Draw semi-transparent background
        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillRect(boxX, boxY, boxWidth, boxHeight);

        // Draw border
        gc.setStroke(Color.rgb(255, 255, 0, 0.6));
        gc.setLineWidth(1);
        gc.strokeRect(boxX, boxY, boxWidth, boxHeight);

        // Draw text
        gc.setFill(Color.rgb(255, 255, 255));
        gc.setFont(javafx.scene.text.Font.font("Arial", 11));
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], boxX + 5, boxY + 15 + i * 15);
        }
    }

    /**
     * Draw highlight for searched star (Phase 5).
     */
    private void drawSearchHighlight(double x, double y) {
        // Draw pulsing green circles
        gc.setStroke(Color.rgb(0, 255, 0, 0.8));
        gc.setLineWidth(3);
        gc.strokeOval(x - 20, y - 20, 40, 40);
        gc.strokeOval(x - 25, y - 25, 50, 50);
    }

    /**
     * Draw deep sky objects from Messier catalog (Phase 8).
     */
    private void drawDeepSkyObjects() {
        if (!displayPrefs.isShowDSO() || deepSkyObjects == null || projection == null) {
            return;
        }

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        for (DeepSkyObject dso : deepSkyObjects) {
            if (!dso.hasValidCoordinates()) continue;

            double[] coords = projection.raDecToScreen(dso.getRa(), dso.getDec());
            if (coords == null) continue;

            double x = coords[0];
            double y = coords[1];

            // Check if on screen
            if (x < 0 || x > width || y < 0 || y > height) continue;

            // Size based on angular size and zoom
            double displaySize = Math.max(5, Math.min(30, dso.getSize() * 0.5));

            // Draw based on object type
            Color color = Color.web(dso.getDisplayColor());
            gc.setStroke(color);
            gc.setLineWidth(2);

            switch (dso.getType().toLowerCase()) {
                case "galaxy":
                    // Draw ellipse for galaxy
                    gc.strokeOval(x - displaySize/2, y - displaySize/3, displaySize, displaySize * 0.66);
                    break;
                case "nebula":
                case "planetary nebula":
                    // Draw fuzzy circle for nebula
                    gc.setStroke(Color.rgb((int)(color.getRed()*255), (int)(color.getGreen()*255),
                                          (int)(color.getBlue()*255), 0.4));
                    gc.strokeOval(x - displaySize, y - displaySize, displaySize * 2, displaySize * 2);
                    gc.strokeOval(x - displaySize/2, y - displaySize/2, displaySize, displaySize);
                    break;
                case "open cluster":
                case "globular cluster":
                    // Draw circle with dots for cluster
                    gc.strokeOval(x - displaySize/2, y - displaySize/2, displaySize, displaySize);
                    gc.setFill(color);
                    for (int i = 0; i < 5; i++) {
                        double angle = Math.random() * 2 * Math.PI;
                        double radius = Math.random() * displaySize/3;
                        gc.fillOval(x + radius * Math.cos(angle) - 1,
                                   y + radius * Math.sin(angle) - 1, 2, 2);
                    }
                    break;
                default:
                    gc.strokeOval(x - displaySize/2, y - displaySize/2, displaySize, displaySize);
            }

            // Draw label for bright objects
            if (dso.getMagnitude() < 7.0) {
                gc.setFill(color);
                gc.setFont(javafx.scene.text.Font.font("Arial", 9));
                gc.fillText(dso.getMessierNumber(), x + displaySize/2 + 3, y - displaySize/2);
            }
        }
    }

    /**
     * Draw planets at calculated positions (Phase 8 - Enhanced with orbital paths and hover).
     */
    private void drawPlanets() {
        if (!displayPrefs.isShowPlanets() || planets == null || projection == null) {
            return;
        }

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        // Draw orbital paths first (behind planets)
        if (showOrbitalPaths) {
            drawOrbitalPaths();
        }

        for (Planet planet : planets) {
            if (!planet.hasValidCoordinates()) continue;

            double[] coords = projection.raDecToScreen(planet.getRa(), planet.getDec());
            if (coords == null) continue;

            double x = coords[0];
            double y = coords[1];

            // Check if on screen (with some margin for labels)
            if (x < -50 || x > width + 50 || y < -50 || y > height + 50) continue;

            // Cache position for potential hover effects
            planet.cachePosition(x, y, true);

            // Draw planet with enhanced rendering (includes hover effects)
            drawPlanet(planet, x, y);
        }
    }

    /**
     * Draw orbital paths for planets (Planet Enhancement).
     */
    private void drawOrbitalPaths() {
        if (planets == null) return;

        // Update orbital paths if needed
        updateOrbitalPaths();

        gc.setLineWidth(1.0);

        for (Planet planet : planets) {
            if (!planet.shouldShowOrbitalPath()) continue;

            List<Planet.OrbitalPoint> path = orbitalPathCache.get(planet.getName());
            if (path == null || path.size() < 2) continue;

            // Set orbital path color (dimmed version of planet color)
            Color planetColor = Color.web(planet.getDisplayColor());
            Color pathColor = Color.rgb(
                (int)(planetColor.getRed() * 255),
                (int)(planetColor.getGreen() * 255),
                (int)(planetColor.getBlue() * 255),
                0.4 // Semi-transparent
            );
            gc.setStroke(pathColor);

            // Draw the orbital path as connected line segments
            Planet.OrbitalPoint prevPoint = null;
            for (Planet.OrbitalPoint point : path) {
                if (prevPoint != null) {
                    double[] coords1 = projection.raDecToScreen(prevPoint.getRa(), prevPoint.getDec());
                    double[] coords2 = projection.raDecToScreen(point.getRa(), point.getDec());

                    if (coords1 != null && coords2 != null) {
                        // Only draw if both points are reasonable (avoid wrapping issues)
                        double dx = coords2[0] - coords1[0];
                        if (Math.abs(dx) < starCanvas.getWidth() / 2) {
                            gc.strokeLine(coords1[0], coords1[1], coords2[0], coords2[1]);
                        }
                    }
                }
                prevPoint = point;
            }
        }
    }

    /**
     * Enhanced planet rendering with realistic colors, sizes, hover effects, and type-specific features
     */
    private void drawPlanet(Planet planet, double x, double y) {
        double size = planet.getDisplaySize();
        Color color = Color.web(planet.getDisplayColor());

        // Enhance size if hovered
        if (planet.isHovered()) {
            size *= 1.3; // Make hovered planets 30% larger
        }

        // Draw planet glow for bright planets or when hovered
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

        // Draw planet disk
        gc.setFill(color);
        gc.fillOval(x - size/2, y - size/2, size, size);

        // Add special rendering for Saturn (rings)
        if (planet.getName().equalsIgnoreCase("Saturn")) {
            drawSaturnRings(x, y, size, color);
        }

        // Add special rendering for Jupiter (bands)
        if (planet.getName().equalsIgnoreCase("Jupiter")) {
            drawJupiterBands(x, y, size, color);
        }

        // Enhanced labeling based on object type
        gc.setFill(Color.rgb(255, 255, 200));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.NORMAL, 10));

        String label = String.format("%s %s", planet.getSymbol(), planet.getName());

        // Add type indicator for non-planets
        if (planet.isDwarfPlanet()) {
            label += " (Dwarf)";
        } else if (planet.isAsteroid()) {
            label += " (Asteroid)";
        }

        gc.fillText(label, x + size/2 + 3, y - size/2 - 2);

        // Draw magnitude and distance info for bright objects or when hovered
        if (planet.getMagnitude() < 8.0 || planet.isHovered()) {
            gc.setFont(javafx.scene.text.Font.font("Arial", 8));
            gc.setFill(Color.rgb(200, 200, 200, 0.8));
            String info = String.format("mag %.1f, %s", planet.getMagnitude(), planet.getDistanceString());
            gc.fillText(info, x + size/2 + 3, y + size/2 + 10);

            // Show orbital period for hovered objects
            if (planet.isHovered() && planet.getOrbitalPeriod() > 0) {
                String periodInfo = String.format("Period: %s", planet.getOrbitalPeriodString());
                gc.fillText(periodInfo, x + size/2 + 3, y + size/2 + 22);
            }
        }
    }

    /**
     * Draw Saturn's rings
     */
    private void drawSaturnRings(double x, double y, double planetSize, Color planetColor) {
        // Outer ring
        gc.setStroke(Color.rgb(200, 180, 120, 0.6));
        gc.setLineWidth(1.0);
        double ringSize = planetSize * 1.8;
        gc.strokeOval(x - ringSize/2, y - ringSize/2, ringSize, ringSize * 0.3); // Flattened ellipse

        // Inner ring
        double innerRingSize = planetSize * 1.4;
        gc.strokeOval(x - innerRingSize/2, y - innerRingSize/2, innerRingSize, innerRingSize * 0.3);
    }

    /**
     * Draw Jupiter's atmospheric bands
     */
    private void drawJupiterBands(double x, double y, double planetSize, Color planetColor) {
        gc.setStroke(Color.rgb(139, 100, 20, 0.4)); // Dark brown bands
        gc.setLineWidth(0.5);

        // Draw horizontal bands across Jupiter
        double bandSpacing = planetSize / 4;
        for (int i = -1; i <= 1; i++) {
            double bandY = y + i * bandSpacing;
            gc.strokeLine(x - planetSize/2, bandY, x + planetSize/2, bandY);
        }
    }

    /**
     * Update orbital paths for planets (Planet Enhancement).
     */
    private void updateOrbitalPaths() {
        LocalDateTime currentTime = timeAnimationRunning ? animationTime : LocalDateTime.now();

        // Check if we need to update orbital paths (daily update or when forced)
        if (lastOrbitalPathUpdate != null &&
            java.time.Duration.between(lastOrbitalPathUpdate, currentTime).toMillis() < ORBITAL_PATH_UPDATE_INTERVAL) {
            return; // Use cached paths
        }

        if (planets == null) return;

        try {
            // Calculate orbital path points for each planet over their orbital period
            for (Planet planet : planets) {
                if (!planet.shouldShowOrbitalPath()) continue;

                List<Planet.OrbitalPoint> pathPoints = new ArrayList<>();

                // Number of points based on orbital period (more points for longer periods)
                int numPoints = Math.min(360, Math.max(36, (int)(planet.getOrbitalPeriod() * 12)));

                // Calculate points over the orbital period
                double periodDays = planet.getOrbitalPeriod() * 365.25;
                LocalDateTime baseTime = currentTime.minusDays((long)(periodDays / 2));

                for (int i = 0; i < numPoints; i++) {
                    double fraction = (double) i / numPoints;
                    LocalDateTime pointTime = baseTime.plusDays((long)(periodDays * fraction));

                    // Calculate planet position at this time
                    List<Planet> tempPlanets = planetService.calculatePlanetPositions(pointTime);
                    Planet tempPlanet = tempPlanets.stream()
                        .filter(p -> p.getName().equals(planet.getName()))
                        .findFirst()
                        .orElse(null);

                    if (tempPlanet != null && tempPlanet.hasValidCoordinates()) {
                        pathPoints.add(new Planet.OrbitalPoint(
                            tempPlanet.getRa(),
                            tempPlanet.getDec(),
                            0, 0 // Screen coordinates will be calculated during rendering
                        ));
                    }
                }

                // Cache the orbital path
                orbitalPathCache.put(planet.getName(), pathPoints);
            }

            lastOrbitalPathUpdate = currentTime;
            log.info("Updated orbital paths for " + orbitalPathCache.size() + " celestial objects");

        } catch (Exception e) {
            log.warning("Error calculating orbital paths: " + e.getMessage());
        }
    }

    /**
     * Update Sun and Moon positions if time has changed (Phase 5 Enhancement).
     */
    private void updateSunMoonPositions() {
        LocalDateTime currentTime = timeAnimationRunning ? animationTime : LocalDateTime.now();

        // Cache positions to avoid recalculating every frame unless time has changed
        if (lastSunMoonUpdate != null &&
            lastSunMoonUpdate.equals(currentTime.withSecond(0).withNano(0))) {
            return; // Use cached positions
        }

        try {
            double lat = parseLatitude();
            double lon = parseLongitude();

            // Calculate Sun position
            currentSunPosition = SunCalculator.calculateSunPosition(currentTime, lat, lon);

            // Calculate Moon position and phase
            currentMoonPosition = MoonCalculator.calculateMoonPosition(currentTime, lat, lon);

            lastSunMoonUpdate = currentTime.withSecond(0).withNano(0);

        } catch (Exception e) {
            log.warning("Error calculating Sun/Moon positions: " + e.getMessage());
        }
    }

    /**
     * Draw the Sun position and status (Phase 5 Enhancement).
     */
    private void drawSun() {
        if (!showSun || currentSunPosition == null || projection == null) {
            return;
        }

        if (currentSunPosition.isVisible()) {
            // Sun is above horizon - draw bright yellow disk
            double[] coords = projection.raDecToScreen(currentSunPosition.getRa(), currentSunPosition.getDec());
            if (coords != null) {
                double x = coords[0];
                double y = coords[1];
                double size = 16; // Larger than stars

                // Check if on screen
                double width = starCanvas.getWidth();
                double height = starCanvas.getHeight();
                if (x >= -size && x <= width + size && y >= -size && y <= height + size) {

                    // Draw bright glow effect
                    gc.setFill(Color.rgb(255, 255, 150, 0.4));
                    gc.fillOval(x - size * 1.5, y - size * 1.5, size * 3, size * 3);

                    // Draw sun disk
                    gc.setFill(Color.rgb(255, 255, 100));
                    gc.fillOval(x - size/2, y - size/2, size, size);

                    // Add corona effect
                    gc.setFill(Color.rgb(255, 245, 200, 0.6));
                    gc.fillOval(x - size/3, y - size/3, size/1.5, size/1.5);

                    // Label
                    gc.setFill(Color.rgb(255, 255, 150));
                    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 12));
                    gc.fillText("☀ Sun", x + size/2 + 5, y - size/2);
                }
            }
        } else {
            // Sun is below horizon - show indicator at bottom of screen
            drawHorizonIndicator("☀ Sun (below horizon)",
                               Color.rgb(255, 200, 100),
                               currentSunPosition.getAltitude());
        }
    }

    /**
     * Draw the Moon position, phase, and status (Phase 5 Enhancement).
     */
    private void drawMoon() {
        if (!showMoon || currentMoonPosition == null || projection == null) {
            return;
        }

        if (currentMoonPosition.isVisible()) {
            // Moon is above horizon - draw with current phase
            double[] coords = projection.raDecToScreen(currentMoonPosition.getRa(), currentMoonPosition.getDec());
            if (coords != null) {
                double x = coords[0];
                double y = coords[1];
                double size = 12;
                double illumination = currentMoonPosition.getIllumination();

                // Check if on screen
                double width = starCanvas.getWidth();
                double height = starCanvas.getHeight();
                if (x >= -size && x <= width + size && y >= -size && y <= height + size) {

                    // Draw moon disk (light gray)
                    gc.setFill(Color.rgb(220, 220, 220, 0.9));
                    gc.fillOval(x - size/2, y - size/2, size, size);

                    // Draw shadow for current phase
                    if (illumination < 0.98) { // Not full moon
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
            // Moon is below horizon - show indicator at bottom of screen
            drawHorizonIndicator(String.format("🌙 Moon (%s, %d%% lit) - below horizon",
                               currentMoonPosition.getPhaseName(),
                               currentMoonPosition.getIlluminationPercent()),
                               Color.rgb(180, 180, 200),
                               currentMoonPosition.getAltitude());
        }
    }

    /**
     * Draw indicator for celestial objects below the horizon (Phase 5 Enhancement).
     */
    private void drawHorizonIndicator(String text, Color color, double altitude) {
        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        // Draw indicator at bottom of screen
        double y = height - 25;
        double x = 20;

        // Semi-transparent background
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(x - 5, y - 15, Math.min(text.length() * 7 + 10, width - 30), 20);

        // Text
        gc.setFill(color);
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.fillText(text, x, y);

        // Show how far below horizon
        if (altitude < 0) {
            String altText = String.format("(%.1f° below horizon)", Math.abs(altitude));
            gc.setFill(Color.rgb(180, 180, 180));
            gc.fillText(altText, x, y + 12);
        }
    }

    /**
     * Draw info overlay with performance metrics (Phase 2, 3, 5).
     */
    private void drawInfo(double latitude, double lst, int visibleStars) {
        gc.setFill(Color.rgb(200, 200, 200, 0.8));
        gc.setFont(javafx.scene.text.Font.font("Arial", 11));

        gc.fillText(String.format("Latitude: %.2f°", latitude), 10, 20);
        gc.fillText(String.format("LST: %.2fh", lst), 10, 35);

        // Show animation time if active (Phase 5)
        if (animationTime != null) {
            String timeStatus = timeAnimationRunning ? "▶" : "⏸";
            // Format time safely to avoid StringIndexOutOfBoundsException
            String timeStr = animationTime.toString();
            // Use substring only if string is long enough, otherwise use full string
            String displayTime = timeStr.length() >= 19 ? timeStr.substring(0, 19) : timeStr;
            gc.fillText(String.format("%s Time: %s (%.0fx)",
                timeStatus,
                displayTime,
                timeAnimationSpeed), 10, 50);
            gc.fillText(String.format("Center: RA %.2fh, Dec %.1f°", viewCenterRA, viewCenterDec), 10, 65);
            gc.fillText(String.format("FOV: %.1f°  Zoom: %.1fx", fieldOfView, zoomLevel), 10, 80);
            gc.fillText(String.format("Visible Stars: %d", visibleStars), 10, 95);
            gc.fillText(String.format("FPS: %.1f", currentFPS), 10, 110);
        } else {
            gc.fillText(String.format("Center: RA %.2fh, Dec %.1f°", viewCenterRA, viewCenterDec), 10, 50);
            gc.fillText(String.format("FOV: %.1f°  Zoom: %.1fx", fieldOfView, zoomLevel), 10, 65);
            gc.fillText(String.format("Visible Stars: %d", visibleStars), 10, 80);
            gc.fillText(String.format("FPS: %.1f", currentFPS), 10, 95);
        }

        int yOffset = animationTime != null ? 125 : 110;

        if (config != null) {
            gc.fillText("Config: " + config.getName(), 10, yOffset);
            gc.fillText("Date: " + config.getDateOfObservation(), 10, yOffset + 15);
            gc.fillText(String.format("Catalog: %,d stars (%s)", currentStarCount, getCatalogDisplayName()), 10, yOffset + 30);
        }

        // Performance indicator
        Color perfColor = currentFPS >= 55 ? Color.GREEN :
                         currentFPS >= 30 ? Color.YELLOW : Color.RED;
        gc.setFill(perfColor);
        gc.fillOval(10, yOffset + 45, 10, 10);

        // Sun and Moon status (Phase 5 Enhancement)
        if (currentSunPosition != null || currentMoonPosition != null) {
            gc.setFill(Color.rgb(200, 200, 200, 0.8));
            gc.setFont(javafx.scene.text.Font.font("Arial", 10));

            int sunMoonOffset = yOffset + 65;

            if (currentSunPosition != null) {
                gc.setFill(currentSunPosition.isVisible() ? Color.rgb(255, 255, 100) : Color.rgb(255, 200, 100));
                gc.fillText(currentSunPosition.getDisplayStatus(), 10, sunMoonOffset);
                sunMoonOffset += 15;
            }

            if (currentMoonPosition != null) {
                gc.setFill(currentMoonPosition.isVisible() ? Color.rgb(200, 200, 220) : Color.rgb(180, 180, 200));
                gc.fillText(currentMoonPosition.getDisplayStatus(), 10, sunMoonOffset);
                sunMoonOffset += 15;
            }

            // Planet status (Enhanced with detailed breakdown)
            if (displayPrefs.isShowPlanets() && planets != null && !planets.isEmpty()) {
                gc.setFill(Color.rgb(200, 200, 200, 0.8));

                // Count different types of objects
                long totalVisible = planets.stream().filter(p -> p.hasValidCoordinates()).count();
                long planetsCount = planets.stream()
                    .filter(p -> p.hasValidCoordinates() && "planet".equals(p.getType())).count();
                long dwarfPlanetsCount = planets.stream()
                    .filter(p -> p.hasValidCoordinates() && "dwarf_planet".equals(p.getType())).count();
                long asteroidsCount = planets.stream()
                    .filter(p -> p.hasValidCoordinates() && "asteroid".equals(p.getType())).count();

                // Enhanced display showing breakdown
                if (dwarfPlanetsCount > 0 || asteroidsCount > 0) {
                    gc.fillText(String.format("🪐 Objects: %d planets, %d dwarf, %d asteroids",
                        planetsCount, dwarfPlanetsCount, asteroidsCount), 10, sunMoonOffset);
                } else {
                    gc.fillText(String.format("🪐 Planets: %d visible", totalVisible), 10, sunMoonOffset);
                }
                sunMoonOffset += 15;

                // Show orbital paths status if enabled
                if (showOrbitalPaths) {
                    gc.setFill(Color.rgb(150, 200, 255, 0.8));
                    gc.fillText("🛸 Orbital paths: ON", 10, sunMoonOffset);
                    sunMoonOffset += 15;
                }
            }

            // View mode status (Dual Hemisphere Enhancement)
            if (currentViewMode != SkyViewMode.SINGLE_HEMISPHERE) {
                gc.setFill(Color.rgb(200, 255, 200, 0.8));
                String viewModeText = "🌐 View: " + currentViewMode.getDisplayName();
                gc.fillText(viewModeText, 10, sunMoonOffset);
                sunMoonOffset += 15;
            }
        }

        // Instructions
        gc.setFill(Color.rgb(200, 200, 200, 0.6));
        gc.setFont(javafx.scene.text.Font.font("Arial", 9));
        String instructions = "Drag: Pan | Scroll: Zoom | Hover: Info | Search: Find stars";
        if (currentViewMode == SkyViewMode.DUAL_HEMISPHERE) {
            instructions += " | View: Dual Hemisphere";
        } else if (currentViewMode == SkyViewMode.FULL_SKY_MERCATOR) {
            instructions += " | View: Full Sky Map";
        }
        gc.fillText(instructions, 10, starCanvas.getHeight() - 10);

        // Draw hemisphere labels for dual hemisphere mode
        if (currentViewMode == SkyViewMode.DUAL_HEMISPHERE) {
            drawHemisphereLabels();
        }
    }

    /**
     * Draw hemisphere labels for dual hemisphere mode.
     */
    private void drawHemisphereLabels() {
        gc.setFill(Color.rgb(255, 255, 255, 0.8));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));

        double width = starCanvas.getWidth();
        double height = starCanvas.getHeight();

        // Northern hemisphere label
        gc.fillText("Northern Hemisphere", width * 0.25 - 60, 25);

        // Southern hemisphere label
        gc.fillText("Southern Hemisphere", width * 0.75 - 60, 25);

        // Draw dividing line
        gc.setStroke(Color.rgb(255, 255, 255, 0.3));
        gc.setLineWidth(1);
        gc.strokeLine(width / 2, 0, width / 2, height);

        // Add celestial pole markers
        gc.setFill(Color.rgb(255, 255, 200, 0.7));
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.fillText("North Celestial Pole", width * 0.25 - 45, height / 2 - 5);
        gc.fillText("South Celestial Pole", width * 0.75 - 45, height / 2 - 5);

        // Draw pole markers
        gc.setFill(Color.rgb(255, 255, 0, 0.8));
        gc.fillOval(width * 0.25 - 2, height / 2 - 2, 4, 4); // North pole
        gc.fillOval(width * 0.75 - 2, height / 2 - 2, 4, 4); // South pole
    }

    /**
     * Parse latitude from config.
     */
    private double parseLatitude() {
        if (config != null && config.getLatitudeDegrees() != null) {
            try {
                return Double.parseDouble(config.getLatitudeDegrees());
            } catch (NumberFormatException e) {
                // Fall through to default
            }
        }
        return 40.0; // Default
    }

    /**
     * Parse sidereal time from config or calculate from animation time (Phase 5).
     */
    private double parseSiderealTime() {
        // If time animation is active or time has been set, calculate LST from animation time
        if (animationTime != null) {
            return calculateLocalSiderealTime(animationTime);
        }

        // Otherwise use config
        if (config != null && config.getSiderealTime() != null) {
            String[] parts = config.getSiderealTime().split(":");
            if (parts.length >= 2) {
                double hours = Double.parseDouble(parts[0]);
                double minutes = Double.parseDouble(parts[1]);
                return hours + (minutes / 60.0);
            }
        }
        return 12.0; // Default
    }

    /**
     * Calculate Local Sidereal Time from date/time and longitude (Phase 5).
     */
    private double calculateLocalSiderealTime(LocalDateTime dateTime) {
        // Get Julian Date (simplified)
        double jd = dateTime.toLocalDate().toEpochDay() + 2440587.5;

        // Calculate Greenwich Mean Sidereal Time (GMST)
        double t = (jd - 2451545.0) / 36525.0;
        double gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0) +
                     0.000387933 * t * t - t * t * t / 38710000.0;

        // Normalize to 0-360
        gmst = gmst % 360.0;
        if (gmst < 0) gmst += 360.0;

        // Add local time of day
        double hoursToday = dateTime.getHour() + dateTime.getMinute() / 60.0 +
                           dateTime.getSecond() / 3600.0;
        double lstDegrees = gmst + 15.0 * hoursToday;

        // Add longitude correction
        double longitude = parseLongitude();
        lstDegrees += longitude;

        // Normalize and convert to hours
        lstDegrees = lstDegrees % 360.0;
        if (lstDegrees < 0) lstDegrees += 360.0;

        return lstDegrees / 15.0;
    }

    /**
     * Parse longitude from config (Phase 5).
     */
    private double parseLongitude() {
        if (config != null && config.getLongitudeDegrees() != null) {
            try {
                return Double.parseDouble(config.getLongitudeDegrees());
            } catch (NumberFormatException e) {
                // Fall through
            }
        }
        return 0.0; // Default
    }

    /**
     * Get display name for current catalog (Enhanced for StarCatalog enum).
     */
    private String getCatalogDisplayName() {
        if (currentCatalog == null) return "Default";

        String filename = currentCatalog.getFilename();
        if (filename.contains("sao")) return "SAO";
        if (filename.contains("100k")) return "100K";
        if (filename.contains("10k")) return "10K";
        if (filename.contains("1k")) return "1K";
        return currentCatalog.getDisplayName();
    }

    /**
     * Search for a star by name and center view on it (Phase 5).
     */
    @FXML
    private void searchStar() {
        String searchQuery = searchField.getText();
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return;
        }

        final String query = searchQuery.trim();

        // Search in all stars (not just visible)
        if (stars == null || stars.getStarList() == null) {
            showSearchError("No star catalog loaded");
            return;
        }

        List<Star> matches = stars.getStarList().stream()
            .filter(s -> s.getName() != null)
            .filter(s -> s.getName().toLowerCase().contains(query.toLowerCase()))
            .sorted(Comparator.comparingDouble(Star::getMag)) // Brightest first
            .limit(10)
            .collect(Collectors.toList());

        if (matches.isEmpty()) {
            showSearchError("No stars found matching: " + query);
            return;
        }

        Star star = matches.get(0); // Take brightest match

        // Store reference for lambda
        final Star foundStar = star;

        // Center view on star
        viewCenterRA = foundStar.getRa();
        viewCenterDec = foundStar.getDec();

        // Zoom in slightly
        if (fieldOfView > 40.0) {
            fieldOfView = 40.0;
            zoomLevel = 120.0 / fieldOfView;
        }

        // Highlight the star
        highlightedStar = foundStar;

        // Trigger recalculation
        needsRecalculation = true;

        log.info("Found star: " + foundStar.getName() + " at RA " + foundStar.getRa() + "h, Dec " + foundStar.getDec() + "°");

        // Show success notification
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Star Found");
            alert.setHeaderText("Found: " + foundStar.getName());
            alert.setContentText(String.format(
                "RA: %.2fh\nDec: %.1f°\nMagnitude: %.1f\n\nView centered on star.",
                foundStar.getRa(), foundStar.getDec(), foundStar.getMag()
            ));
            alert.showAndWait();
        });
    }

    /**
     * Show search error dialog (Phase 5).
     */
    private void showSearchError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Search Failed");
        alert.setHeaderText("Star not found");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clear search field and highlight (Phase 5).
     */
    @FXML
    private void clearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        highlightedStar = null;
        log.info("Search cleared");
    }

    /**
     * Toggle time animation on/off (Phase 5, enhanced in Phase 9).
     */
    @FXML
    private void toggleTimeAnimation() {
        timeAnimationRunning = !timeAnimationRunning;

        if (timeAnimationRunning) {
            // Start animation
            if (animationTime == null) {
                animationTime = LocalDateTime.now();
                originalTime = animationTime;
            }
            if (playButton != null) {
                playButton.setText("Pause");
            }
            log.info("Time animation started at " + animationTime);
        } else {
            if (playButton != null) {
                playButton.setText("Play");
            }
            log.info("Time animation paused");
        }
    }

    /**
     * Reset time to current real time (Phase 5, enhanced in Phase 9).
     */
    @FXML
    private void resetTime() {
        animationTime = LocalDateTime.now();
        originalTime = animationTime;
        timeAnimationRunning = false;
        accumulatedSeconds = 0.0; // Reset accumulator
        lastPlanetUpdate = null; // Reset planet update tracker
        if (playButton != null) {
            playButton.setText("Play");
        }
        updatePlanetPositions();
        needsRecalculation = true;
        log.info("Time reset to current: " + animationTime);
    }

    /**
     * Setup speed slider for variable animation speed (Phase 9).
     */
    private void setupSpeedSlider() {
        if (speedSlider != null && speedLabel != null) {
            // Initialize slider
            speedSlider.setValue(timeAnimationSpeed);
            updateSpeedLabel(timeAnimationSpeed);

            // Add listener for speed changes
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                timeAnimationSpeed = newVal.doubleValue();
                updateSpeedLabel(timeAnimationSpeed);
            });
        }
    }

    /**
     * Update speed label text (Phase 9).
     */
    private void updateSpeedLabel(double speed) {
        if (speedLabel != null) {
            String speedText;
            if (speed < 60) {
                speedText = String.format("Speed: %.0fx", speed);
            } else if (speed < 1440) {
                speedText = String.format("Speed: %.0fx (%.1fh/s)", speed, speed / 60.0);
            } else {
                speedText = String.format("Speed: %.0fx (%.1fd/s)", speed, speed / 1440.0);
            }
            speedLabel.setText(speedText);
        }
    }

    /**
     * Show date/time picker dialog (Phase 9).
     */
    @FXML
    private void showDateTimePicker() {
        javafx.scene.control.Dialog<LocalDateTime> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Set Date & Time");
        dialog.setHeaderText("Choose observation date and time");

        // Create dialog content
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        // Date picker
        javafx.scene.control.DatePicker datePicker = new javafx.scene.control.DatePicker();
        if (animationTime != null) {
            datePicker.setValue(animationTime.toLocalDate());
        } else {
            datePicker.setValue(java.time.LocalDate.now());
        }

        // Time fields
        javafx.scene.control.Spinner<Integer> hourSpinner =
            new javafx.scene.control.Spinner<>(0, 23,
                animationTime != null ? animationTime.getHour() : LocalDateTime.now().getHour());
        hourSpinner.setEditable(true);
        hourSpinner.setPrefWidth(80);

        javafx.scene.control.Spinner<Integer> minuteSpinner =
            new javafx.scene.control.Spinner<>(0, 59,
                animationTime != null ? animationTime.getMinute() : LocalDateTime.now().getMinute());
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(80);

        javafx.scene.control.Spinner<Integer> secondSpinner =
            new javafx.scene.control.Spinner<>(0, 59,
                animationTime != null ? animationTime.getSecond() : LocalDateTime.now().getSecond());
        secondSpinner.setEditable(true);
        secondSpinner.setPrefWidth(80);

        // Add to grid
        grid.add(new javafx.scene.control.Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0, 3, 1);

        grid.add(new javafx.scene.control.Label("Time:"), 0, 1);
        grid.add(new javafx.scene.control.Label("Hour"), 1, 1);
        grid.add(hourSpinner, 1, 2);
        grid.add(new javafx.scene.control.Label("Minute"), 2, 1);
        grid.add(minuteSpinner, 2, 2);
        grid.add(new javafx.scene.control.Label("Second"), 3, 1);
        grid.add(secondSpinner, 3, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        javafx.scene.control.ButtonType setButtonType =
            new javafx.scene.control.ButtonType("Set Time", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(setButtonType, javafx.scene.control.ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == setButtonType) {
                return LocalDateTime.of(
                    datePicker.getValue(),
                    java.time.LocalTime.of(
                        hourSpinner.getValue(),
                        minuteSpinner.getValue(),
                        secondSpinner.getValue()
                    )
                );
            }
            return null;
        });

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(dateTime -> {
            animationTime = dateTime;
            originalTime = dateTime;
            timeAnimationRunning = false;
            accumulatedSeconds = 0.0; // Reset accumulator
            lastPlanetUpdate = null; // Reset planet update tracker
            updatePlanetPositions();
            needsRecalculation = true;
            log.info("Time set to: " + animationTime);

            // Show confirmation
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Time Set");
            alert.setHeaderText(null);
            alert.setContentText("Observation time set to:\n" +
                dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            alert.showAndWait();
        });
    }


    /**
     * Toggle Sun display on/off (Phase 5 Enhancement).
     */
    @FXML
    private void toggleSun() {
        showSun = !showSun;
        if (sunMenuItem != null) {
            sunMenuItem.setSelected(showSun);
        }
        log.info("Sun display " + (showSun ? "enabled" : "disabled"));
    }

    /**
     * Toggle Moon display on/off (Phase 5 Enhancement).
     */
    @FXML
    private void toggleMoon() {
        showMoon = !showMoon;
        if (moonMenuItem != null) {
            moonMenuItem.setSelected(showMoon);
        }
        log.info("Moon display " + (showMoon ? "enabled" : "disabled"));
    }

    /**
     * Toggle Planets on/off (Phase 8).
     */
    @FXML
    private void togglePlanets() {
        displayPrefs.setShowPlanets(!displayPrefs.isShowPlanets());
        if (planetsMenuItem != null) {
            planetsMenuItem.setSelected(displayPrefs.isShowPlanets());
        }
        log.info("Planets " + (displayPrefs.isShowPlanets() ? "enabled" : "disabled"));
    }

    /**
     * Toggle Constellations on/off.
     */
    @FXML
    private void toggleConstellations() {
        displayPrefs.setShowConstellations(!displayPrefs.isShowConstellations());
        if (constellationsMenuItem != null) {
            constellationsMenuItem.setSelected(displayPrefs.isShowConstellations());
        }
        log.info("Constellations " + (displayPrefs.isShowConstellations() ? "enabled" : "disabled"));
    }

    /**
     * Toggle Grid on/off (Phase 6).
     */
    @FXML
    private void toggleGrid() {
        displayPrefs.setShowGrid(!displayPrefs.isShowGrid());
        if (gridMenuItem != null) {
            gridMenuItem.setSelected(displayPrefs.isShowGrid());
        }
        log.info("RA/Dec grid " + (displayPrefs.isShowGrid() ? "enabled" : "disabled"));
    }

    /**
     * Toggle Deep Sky Objects on/off (Phase 8).
     */
    @FXML
    private void toggleDeepSky() {
        displayPrefs.setShowDSO(!displayPrefs.isShowDSO());
        if (dsoMenuItem != null) {
            dsoMenuItem.setSelected(displayPrefs.isShowDSO());
        }
        log.info("Deep Sky Objects " + (displayPrefs.isShowDSO() ? "enabled" : "disabled"));
    }

    /**
     * Toggle Orbital Paths on/off (Planet Enhancement).
     */
    @FXML
    private void toggleOrbitalPaths() {
        showOrbitalPaths = !showOrbitalPaths;
        if (orbitalPathsMenuItem != null) {
            orbitalPathsMenuItem.setSelected(showOrbitalPaths);
        }

        // Clear existing orbital path cache when toggled
        if (!showOrbitalPaths) {
            orbitalPathCache.clear();
        } else {
            // Force recalculation of orbital paths
            lastOrbitalPathUpdate = null;
        }

        log.info("Orbital Paths " + (showOrbitalPaths ? "enabled" : "disabled"));
    }

    /**
     * Set single hemisphere view mode (Dual Hemisphere Enhancement).
     */
    @FXML
    private void setSingleHemisphere() {
        currentViewMode = SkyViewMode.SINGLE_HEMISPHERE;
        updateViewMode();
        log.info("View mode set to: Single Hemisphere");
    }

    /**
     * Set dual hemisphere view mode (Dual Hemisphere Enhancement).
     */
    @FXML
    private void setDualHemisphere() {
        currentViewMode = SkyViewMode.DUAL_HEMISPHERE;
        updateViewMode();
        log.info("View mode set to: Dual Hemisphere");
    }

    /**
     * Set full sky Mercator view mode (Dual Hemisphere Enhancement).
     */
    @FXML
    private void setFullSky() {
        currentViewMode = SkyViewMode.FULL_SKY_MERCATOR;
        updateViewMode();
        log.info("View mode set to: Full Sky Mercator");
    }

    /**
     * Update projection and trigger re-rendering when view mode changes.
     */
    private void updateViewMode() {
        if (projection != null) {
            projection.setViewMode(currentViewMode);
            needsRecalculation = true; // Force complete recalculation

            // Clear caches as coordinates will be completely different
            visibleStarsCache = null;
            orbitalPathCache.clear();
            lastOrbitalPathUpdate = null;
        }
    }

    /**
     * Load deep sky objects from Messier catalog (Phase 8).
     */
    private void loadDeepSkyObjects() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            File dsoFile = new File("messier_catalog.json");

            if (dsoFile.exists()) {
                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(dsoFile);
                com.fasterxml.jackson.databind.JsonNode objects = root.get("objects");

                deepSkyObjects = new java.util.ArrayList<>();
                if (objects != null && objects.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode node : objects) {
                        DeepSkyObject dso = DeepSkyObject.builder()
                                .messierNumber(node.get("messierNumber").asText())
                                .name(node.get("name").asText())
                                .type(node.get("type").asText())
                                .ra(node.get("ra").asDouble())
                                .dec(node.get("dec").asDouble())
                                .magnitude(node.get("magnitude").asDouble())
                                .size(node.get("size").asDouble())
                                .constellation(node.get("constellation").asText())
                                .build();
                        deepSkyObjects.add(dso);
                    }
                }
                log.info("Loaded " + deepSkyObjects.size() + " deep sky objects");
            } else {
                deepSkyObjects = new java.util.ArrayList<>();
                log.warning("Messier catalog not found: " + dsoFile.getAbsolutePath());
            }
        } catch (Exception e) {
            deepSkyObjects = new java.util.ArrayList<>();
            log.warning("Failed to load deep sky objects: " + e.getMessage());
        }
    }

    /**
     * Update planet positions for current animation time (Phase 8).
     */
    private void updatePlanetPositions() {
        if (planetService != null) {
            LocalDateTime timeToUse = timeAnimationRunning ? animationTime : LocalDateTime.now();
            planets = planetService.calculatePlanetPositions(timeToUse);
            if (planets != null) {
                log.info("Calculated positions for " + planets.size() + " planets");
            }
        }
    }

    /**
     * Show catalog selection dialog (Phase 7 - Enhanced).
     */
    @FXML
    private void selectCatalog() {
        // Create choice dialog with all available catalogs
        javafx.scene.control.ChoiceDialog<StarCatalog> dialog =
            new javafx.scene.control.ChoiceDialog<>(currentCatalog, StarCatalog.values());

        dialog.setTitle("Select Star Catalog");
        dialog.setHeaderText("Choose a star catalog");
        dialog.setContentText("Available catalogs:");

        // Customize the dialog to show detailed information
        dialog.getDialogPane().setExpandableContent(createCatalogInfoPane());
        dialog.getDialogPane().setExpanded(true);

        // Show the dialog and get the result
        java.util.Optional<StarCatalog> result = dialog.showAndWait();

        if (result.isPresent() && result.get() != currentCatalog) {
            StarCatalog selectedCatalog = result.get();
            loadCatalog(selectedCatalog);
        }
    }

    /**
     * Create an information pane showing catalog details
     */
    private javafx.scene.layout.VBox createCatalogInfoPane() {
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(10));

        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Catalog Information:");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        vbox.getChildren().add(titleLabel);

        // Create table of catalog info
        javafx.scene.control.TableView<StarCatalog> table = new javafx.scene.control.TableView<>();
        table.setPrefHeight(200);

        // Name column
        javafx.scene.control.TableColumn<StarCatalog, String> nameCol =
            new javafx.scene.control.TableColumn<>("Catalog");
        nameCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDisplayName()));
        nameCol.setPrefWidth(150);

        // Star count column
        javafx.scene.control.TableColumn<StarCatalog, String> countCol =
            new javafx.scene.control.TableColumn<>("Stars");
        countCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.format("%,d", data.getValue().getStarCount())));
        countCol.setPrefWidth(80);

        // Memory column
        javafx.scene.control.TableColumn<StarCatalog, String> memoryCol =
            new javafx.scene.control.TableColumn<>("Memory");
        memoryCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.format("%.1f MB", data.getValue().getEstimatedMemoryMB())));
        memoryCol.setPrefWidth(80);

        // Performance column
        javafx.scene.control.TableColumn<StarCatalog, String> perfCol =
            new javafx.scene.control.TableColumn<>("Performance");
        perfCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getPerformanceCategory()));
        perfCol.setPrefWidth(150);

        table.getColumns().addAll(nameCol, countCol, memoryCol, perfCol);
        table.getItems().addAll(StarCatalog.values());

        vbox.getChildren().add(table);

        // Add recommendations
        javafx.scene.control.Label recLabel = new javafx.scene.control.Label("Recommendations:");
        recLabel.setStyle("-fx-font-weight: bold;");
        vbox.getChildren().add(recLabel);

        javafx.scene.control.Label rec1 = new javafx.scene.control.Label("• Education/Classroom: 1K-10K stars");
        javafx.scene.control.Label rec2 = new javafx.scene.control.Label("• Amateur Astronomy: 5K-25K stars");
        javafx.scene.control.Label rec3 = new javafx.scene.control.Label("• Professional/Research: 25K+ stars");
        javafx.scene.control.Label rec4 = new javafx.scene.control.Label("• Performance Concerns: Use 5K or fewer");

        vbox.getChildren().addAll(rec1, rec2, rec3, rec4);

        return vbox;
    }

    /**
     * Load a new star catalog (enhanced version with StarCatalog enum)
     */
    private void loadCatalog(StarCatalog catalog) {
        log.info("Loading catalog: " + catalog.getDisplayName());

        // Show loading dialog for large catalogs
        javafx.scene.control.Alert loadingAlert = null;
        if (catalog.getStarCount() > 25000) {
            loadingAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            loadingAlert.setTitle("Loading Catalog");
            loadingAlert.setHeaderText("Loading " + catalog.getDisplayName());
            loadingAlert.setContentText("Please wait while " + String.format("%,d", catalog.getStarCount()) + " stars are loaded...");
            loadingAlert.show();
        }

        try {
            // Load the catalog in the star service
            starService.loadCatalog(catalog);

            // Get the actual loaded catalog from StarService (in case it fell back)
            this.currentCatalog = starService.getCurrentCatalog();
            this.stars = starService.getStars();
            this.currentStarCount = starService.getCurrentStarCount();

            // Verify we actually loaded the requested catalog
            if (this.currentCatalog != catalog) {
                throw new RuntimeException("Failed to load requested catalog. Currently loaded: " +
                    this.currentCatalog.getDisplayName() + " with " + this.currentStarCount + " stars");
            }

            // Force complete recalculation
            needsRecalculation = true;
            visibleStarsCache = null;

            // Close loading dialog
            if (loadingAlert != null) {
                loadingAlert.close();
            }

            // Show success message with actual loaded data
            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            successAlert.setTitle("Catalog Loaded");
            successAlert.setHeaderText("Successfully loaded " + this.currentCatalog.getDisplayName());
            successAlert.setContentText(String.format("Loaded %,d stars. Performance: %s",
                currentStarCount, this.currentCatalog.getPerformanceCategory()));
            successAlert.showAndWait();

        } catch (Exception e) {
            if (loadingAlert != null) {
                loadingAlert.close();
            }

            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            errorAlert.setTitle("Catalog Load Failed");
            errorAlert.setHeaderText("Failed to load " + catalog.getDisplayName());
            errorAlert.setContentText("Error: " + e.getMessage() + "\n\nPlease check console for details.");
            errorAlert.showAndWait();

            log.warning("Failed to load catalog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load a star catalog by filename (backward compatibility method)
     */
    private void loadCatalogByFilename(String filename) {
        // Find matching StarCatalog enum if possible
        for (StarCatalog catalog : StarCatalog.values()) {
            if (catalog.getFilename().equals(filename)) {
                loadCatalog(catalog);
                return;
            }
        }

        // Fallback for unknown files - log and continue with basic loading
        log.warning("Unknown catalog file: " + filename + ". Using basic loading.");

        // For backward compatibility, keep some basic file loading
        // This ensures cycleCatalog() still works with any filename
        try {
            // Basic file loading without StarService enhancement
            currentStarCount = 0; // Will be updated when file is actually loaded
            needsRecalculation = true;
            visibleStarsCache = null;

            log.info("Attempted to load catalog: " + filename);
        } catch (Exception e) {
            log.warning("Failed to load catalog " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Get human-readable description for catalog file (Phase 7).
     */
    private String getCatalogDescription(String filename) {
        // Check specific filenames first
        if (filename.equals("stars.json")) {
            return "Original - 166 bright stars";
        } else if (filename.equals("stars_sao_fk5.json")) {
            return "FK5 Cross Index - 1,535 real stars";
        } else if (filename.equals("stars_with_spectral.json")) {
            return "Spectral Test - 18 stars";
        } else if (filename.equals("stars_sao.json")) {
            return "SAO Catalog - 37,539 real stars";
        }
        // Then check partial matches
        else if (filename.contains("100k")) {
            return "Large Synthetic - 100,000 stars";
        } else if (filename.contains("10k")) {
            return "Medium Synthetic - 10,000 stars";
        } else if (filename.contains("1k")) {
            return "Small Synthetic - 1,000 stars";
        } else if (filename.contains("spectral")) {
            return "Spectral Test - few stars";
        } else if (filename.contains("backup")) {
            return "Backup Catalog";
        } else if (filename.contains("sao")) {
            return "SAO-related catalog";
        } else {
            return filename;
        }
    }

    /**
     * Cycle through available star catalogs (Phase 7 - kept for compatibility).
     */
    @FXML
    private void cycleCatalog() {
        // Available catalogs in order
        String[] catalogs = {
            "stars_sao.json",       // 37,539 stars (SAO catalog - real data!)
            "stars.json",           // 166 stars (original bright stars)
            "stars_1k.json",        // 1,000 brightest stars
            "stars_10k.json",       // 10,000 stars
            "stars_100k.json",      // 100,000 stars (HYG database subset)
            "stars_sao_fk5.json",   // 15,349 stars (SAO with FK5 coords - real data)
            "stars_with_spectral.json" // few stars with spectral data
        };

        // Find current index based on filename
        int currentIndex = 0;
        String currentFilename = currentCatalog != null ? currentCatalog.getFilename() : "stars.json";
        for (int i = 0; i < catalogs.length; i++) {
            if (catalogs[i].equals(currentFilename)) {
                currentIndex = i;
                break;
            }
        }

        // Move to next catalog (cycle back to first after last)
        int nextIndex = (currentIndex + 1) % catalogs.length;
        String nextCatalog = catalogs[nextIndex];

        // Try to load the catalog
        loadCatalogByFilename(nextCatalog);
    }

    /**
     * Export current view as screenshot (Phase 4).
     */
    @FXML
    private void exportScreenshot() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Planetarium Screenshot");
        fileChooser.setInitialFileName("planetarium_" + System.currentTimeMillis() + ".png");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG Image", "*.png"),
            new FileChooser.ExtensionFilter("JPEG Image", "*.jpg")
        );

        File file = fileChooser.showSaveDialog(starCanvas.getScene().getWindow());

        if (file != null) {
            try {
                // Capture canvas as image
                WritableImage image = starCanvas.snapshot(null, null);

                // Determine format from extension
                String format = file.getName().toLowerCase().endsWith(".jpg") ? "jpg" : "png";

                // Save to file
                ImageIO.write(
                    SwingFXUtils.fromFXImage(image, null),
                    format,
                    file
                );

                log.info("Screenshot saved: " + file.getAbsolutePath());

                // Show success notification
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Screenshot Saved");
                alert.setHeaderText(null);
                alert.setContentText("Screenshot saved successfully:\n" + file.getName());
                alert.showAndWait();

            } catch (IOException e) {
                log.warning("Failed to save screenshot: " + e.getMessage());

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText("Failed to save screenshot");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void switchToConfig(ActionEvent event) throws IOException {
        Main.setRoot("config");
    }

    @FXML
    private void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exit application");
        alert.setContentText("Do you want to exit application?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Exiting application.");
            stopRenderLoop();
            Platform.exit();
            System.exit(0);
        }
    }
}

