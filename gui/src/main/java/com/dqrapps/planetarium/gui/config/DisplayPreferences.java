package com.dqrapps.planetarium.gui.config;

/**
 * Holds display preferences for the star plot.
 */
public class DisplayPreferences {
    private static DisplayPreferences instance;

    private boolean showConstellations = true;
    private boolean showGrid = false;
    private boolean showDSO = true;
    private boolean showPlanets = true;

    private DisplayPreferences() {}

    public static DisplayPreferences getInstance() {
        if (instance == null) {
            instance = new DisplayPreferences();
        }
        return instance;
    }

    // Getters and setters
    public boolean isShowConstellations() {
        return showConstellations;
    }

    public void setShowConstellations(boolean showConstellations) {
        this.showConstellations = showConstellations;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowDSO() {
        return showDSO;
    }

    public void setShowDSO(boolean showDSO) {
        this.showDSO = showDSO;
    }

    public boolean isShowPlanets() {
        return showPlanets;
    }

    public void setShowPlanets(boolean showPlanets) {
        this.showPlanets = showPlanets;
    }
}

