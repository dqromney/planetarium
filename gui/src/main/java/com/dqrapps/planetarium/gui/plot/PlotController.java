package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import javafx.fxml.FXML;

import java.io.IOException;

public class PlotController {

    @FXML
    private void switchToSplash() throws IOException {
        Main.setRoot("splash");
    }

    @FXML
    private void switchToConfig() throws IOException {
        Main.setRoot("config");
    }

}
