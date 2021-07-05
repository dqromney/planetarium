package com.dqrapps.planetarium.gui.splash;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.dqrapps.planetarium.logic.service.ConfigService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
// JavaFX Splash Screen video
// https://www.genuinecoder.com/javafx-splash-screen-loading-screen/#:~:text=JavaFX%20Splash%20Screen%20Splash%20screens%20are%20awesome.%20They,JavaFX%20Splash%20Screen%20%2F%20Loading%20Screen%20with%20animation.
public class SplashController implements Initializable {

    @FXML
    private static Label progress = new Label("Loading ...");

    public static Label label;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label = progress;
    }

    @FXML
    private void switchToConfig() throws IOException {
        Main.setRoot("config");
    }

}
