package com.dqrapps.planetarium.gui.splash;

import com.dqrapps.planetarium.gui.App;
import javafx.fxml.FXML;

import java.io.IOException;

public class SplashController {

    @FXML
    private void switchToConfig() throws IOException {
        App.setRoot("config");
    }
}
