package com.dqrapps.planetarium.logic.type;

public enum PlotMode {
    INDIVIDUAL("Individual"),
    CONTINUOUS("Continuous");

    private String mode;

    PlotMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
