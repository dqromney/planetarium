package com.dqrapps.planetarium.gui.splash;

import com.dqrapps.planetarium.gui.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
