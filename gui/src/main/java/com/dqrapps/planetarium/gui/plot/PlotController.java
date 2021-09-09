package com.dqrapps.planetarium.gui.plot;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Coordinate;
import com.dqrapps.planetarium.logic.model.Hemisphere;
import com.dqrapps.planetarium.logic.model.Stars;
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
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

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

    @SneakyThrows
    @FXML
    private void initialize() {
        configService = ConfigService.getInstance();
        config = configService.getCurrentConfig();
        starService = StarService.getInstance();
        if (starService.defaultStarsExists()) {
            stars = starService.getStars();
        }
        astroService = new AstroService();
        Hemisphere hemisphere = Hemisphere.valueOf(config.getHorizon().toUpperCase());
        coordinateList = new ArrayList<>();
        stars.getStarList().forEach(star -> {
            Coordinate coordinate = astroService.getCoordinate(hemisphere, config, star);
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
        gc.setFill(Color.GRAY);
        this.coordinateList.forEach(item -> {
            gc.fillOval(item.getX(), item.getY(), 2, 2);
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

}
