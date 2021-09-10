package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.*;
import com.dqrapps.planetarium.logic.service.AstroService;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.service.StarService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log
public class PlotController {

    private ConfigService configService;
    private StarService starService;
    private Config config;
    private Stars stars;
    private AstroService astroService;

    @FXML
    private Canvas canvas;

    private List<Coordinate> coordinateList;

    // Called each time window is opened; will use current config.
    @SneakyThrows
    @FXML
    private void initialize() {
        configService = ConfigService.getInstance();
        config = configService.getCurrentConfig();
        starService = StarService.getInstance();
        if (starService.defaultStarsExists()) {
            stars = starService.getStars();
        }
        astroService = new AstroService(new Screen(canvas.getWidth(), canvas.getHeight()));
        Horizon horizon = Horizon.valueOf(config.getHorizon().toUpperCase());
        coordinateList = new ArrayList<>();
        stars.getStarList().forEach(star -> {
            Coordinate coordinate = astroService.getCoordinate(horizon, config, star);
            if (coordinate != null) {
                this.coordinateList.add(coordinate);
            }
        });
        this.renderStars();
    }

    @FXML
    public void switchToConfig() throws IOException {
        Scene priorScene = Main.getCurrentScene();
        log.info(String.format("plot width/height: %1$.0f/%2$.0f", priorScene.getWidth(), priorScene.getHeight()));
        Main.setRoot("config");
        Scene currentScene = Main.getCurrentScene();
        log.info(String.format("config width/height: %1$.0f/%2$.0f", currentScene.getWidth(), currentScene.getHeight()));
        currentScene.getWindow().setHeight(450);
        currentScene.getWindow().setWidth(525);
    }

    @FXML
    public void renderStars() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.setLineWidth(2.0);
        gc.setStroke(Color.GRAY);
        gc.setFill(Color.WHITE);
        this.coordinateList.forEach(item -> {
            if (item.getMag() > 3.0) {
                gc.fillOval(item.getX(), item.getY(), 1, 1);
            } else if (item.getMag() > 1.5 && item.getMag() <= 3.0) {
                gc.fillOval(item.getX(), item.getY(), 2, 2);
            } else if (item.getMag() > -0.5 && item.getMag() <= 1.5) {
                gc.fillOval(item.getX(), item.getY(), 3, 3);
            } else {
                gc.fillOval(item.getX(), item.getY(), 4, 4);
            }
        });
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

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
