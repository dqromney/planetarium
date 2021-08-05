package com.dqrapps.planetarium.gui;

import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.service.StarService;
import javafx.stage.Stage;

public class CalculateAndPlot {

    private StarService starService;
    private Stars stars;

    public CalculateAndPlot() {
        starService = StarService.getInstance();
        stars = starService.getStars();
    }

    public void show() {
    }

    public void doStarChart(Stage pStage) {
    }
}
