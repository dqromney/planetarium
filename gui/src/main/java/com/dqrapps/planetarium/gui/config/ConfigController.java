package com.dqrapps.planetarium.gui.config;

import com.dqrapps.planetarium.gui.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class ConfigController {

    ObservableList<String> plotModeList = FXCollections
            .observableArrayList("North", "South");

    @FXML
    private ComboBox plotModeBox;

    @FXML
    private void initialize() {
        plotModeBox.setValue("North");
        plotModeBox.setItems(plotModeList);
    }

    @FXML
    private void switchToPlot() throws IOException {
        App.setRoot("plot");
    }
}
