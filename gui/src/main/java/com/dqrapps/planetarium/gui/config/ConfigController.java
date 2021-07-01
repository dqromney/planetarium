package com.dqrapps.planetarium.gui.config;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.type.Horizon;
import com.dqrapps.planetarium.logic.type.PlotMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class ConfigController {

    ObservableList<String> horizonList = FXCollections
            .observableArrayList(Horizon.NORTH.getToken(), Horizon.SOUTH.getToken());
    ObservableList<String> plotModeList = FXCollections
            .observableArrayList(PlotMode.INDIVIDUAL.getMode(), PlotMode.CONTINUOUS.getMode());

    @FXML
    private ComboBox horizonBox;
    @FXML
    private ComboBox plotModeBox;

    @FXML
    private void initialize() {
        horizonBox.setValue(Horizon.NORTH.getToken());
        horizonBox.setItems(horizonList);
        plotModeBox.setValue(PlotMode.INDIVIDUAL.getMode());
        plotModeBox.setItems(plotModeList);
    }

    @FXML
    private void switchToPlot() throws IOException {
        Main.setRoot("plot");
    }
}
