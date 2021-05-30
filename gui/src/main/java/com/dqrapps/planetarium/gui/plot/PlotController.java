package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.App;
import javafx.fxml.FXML;

import java.io.IOException;

public class PlotController {

    @FXML
    private void switchToSplash() throws IOException {
        App.setRoot("splash");
    }

    @FXML
    private void switchToConfig() throws IOException {
        App.setRoot("config");
    }

}
