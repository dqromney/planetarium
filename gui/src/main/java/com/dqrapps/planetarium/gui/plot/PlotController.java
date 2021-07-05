package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;

public class PlotController {

    private Config config;

    @FXML
    public void switchToConfig() throws IOException {
        Main.setRoot("config");
    }

    @FXML
    private void exit() {
        System.out.println("Exiting application.");
        Platform.exit();
        System.exit(0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------
    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
