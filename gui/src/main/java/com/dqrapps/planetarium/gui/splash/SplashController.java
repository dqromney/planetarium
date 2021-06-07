package com.dqrapps.planetarium.gui.splash;

import com.dqrapps.planetarium.gui.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
// JavaFX Splash Screen video
// https://www.genuinecoder.com/javafx-splash-screen-loading-screen/#:~:text=JavaFX%20Splash%20Screen%20Splash%20screens%20are%20awesome.%20They,JavaFX%20Splash%20Screen%20%2F%20Loading%20Screen%20with%20animation.
public class SplashController implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private void switchToConfig() throws IOException {
        App.setRoot("config");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
