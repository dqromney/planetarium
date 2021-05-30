package com.dqrapps.planetarium.gui.config;

import com.dqrapps.planetarium.gui.App;
import javafx.fxml.FXML;

import java.io.IOException;

public class ConfigController {

    @FXML
    private void switchToPlot() throws IOException {
        App.setRoot("plot");
    }
}
