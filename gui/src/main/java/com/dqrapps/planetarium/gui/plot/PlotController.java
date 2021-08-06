package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.service.StarService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.SneakyThrows;

import java.io.IOException;

public class PlotController {

    private ConfigService configService;
    private StarService starService;
    private Config config;
    private Stars stars;

    @SneakyThrows
    @FXML
    private void initialize() {
        configService = ConfigService.getInstance();
        config = configService.getCurrentConfig();
        starService = StarService.getInstance();
        if (starService.defaultStarsExists()) {
            stars = starService.getStars();
        }
    }

    @FXML
    public void switchToConfig() throws IOException {
        Scene priorScene = Main.getCurrentScene();
        System.out.println("plot width/height:");
        System.out.println(priorScene.getWidth());
        System.out.println(priorScene.getHeight());
        Main.setRoot("config");
        Scene currentScene = Main.getCurrentScene();
        System.out.println("config width/height:");
        System.out.println(currentScene.getWidth());
        System.out.println(currentScene.getHeight());
        currentScene.getWindow().setHeight(450);
        currentScene.getWindow().setWidth(525);
    }

    @FXML
    private void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exit application");
        alert.setContentText("Do you want to exit application?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Exiting application.");
            Platform.exit();
            System.exit(0);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------

}
