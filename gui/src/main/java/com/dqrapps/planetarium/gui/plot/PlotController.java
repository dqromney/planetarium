package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Stars;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.service.StarService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

public class PlotController {

    private Config config;
    private Stars stars;

    public PlotController() throws IOException {
//        this.config = new ConfigService().loadConfig("./configs.json");
//        this.stars = new StarService().loadStars("./data/stars.json");
//        this.showConfigAndStars();
    }

    @FXML
    public void switchToConfig() throws IOException {
        Main.setRoot("config");
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

    private void showConfigAndStars() {
        System.out.println(config.toString());
        this.stars.getStarList().forEach(s -> System.out.println(s) );
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
