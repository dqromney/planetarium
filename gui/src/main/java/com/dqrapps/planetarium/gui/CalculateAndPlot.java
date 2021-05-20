package com.dqrapps.planetarium.gui;

import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.service.StarService;

import java.io.IOException;

public class CalculateAndPlot {

    private StarService starService;

    public CalculateAndPlot() {
        starService = new StarService();
    }

    public void show() {
        try {
            Stars stars = starService.loadStars("stars.json");
            stars.getStarList().stream().forEach(s -> {
                System.out.println(s);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
