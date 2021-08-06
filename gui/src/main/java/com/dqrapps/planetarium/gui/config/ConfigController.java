package com.dqrapps.planetarium.gui.config;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.type.Horizon;
import com.dqrapps.planetarium.logic.type.PlotMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

public class ConfigController {

    ConfigService configService;

    ObservableList<String> horizonList = FXCollections
            .observableArrayList(Horizon.NORTH.getToken(), Horizon.SOUTH.getToken());
    ObservableList<String> plotModeList = FXCollections
            .observableArrayList(PlotMode.INDIVIDUAL.getMode(), PlotMode.CONTINUOUS.getMode());

    @FXML
    private ComboBox horizonBox;
    @FXML
    private ComboBox plotModeBox;
    @FXML
    private MenuButton loadConfigMenu;

    @FXML
    private TextField longHours;
    @FXML
    private TextField longMinutes;
    @FXML
    private TextField latHours;
    @FXML
    private DatePicker viewDate;
    @FXML
    private TextField siderealTime;
    @FXML
    private TextField configName;

    private Configs configs;

    /**
     * Initialize page each time it is opened.
     */
    @SneakyThrows
    @FXML
    private void initialize() {
        this.configService = ConfigService.getInstance();

        horizonBox.setItems(horizonList);
        plotModeBox.setItems(plotModeList);

        this.configs = configService.getConfigs();
        // Populate Load Button Menu items
        configs.getConfigList().forEach(c -> {
            if (!c.getName().equalsIgnoreCase("Default")) {
                loadConfigMenu.getItems().add(new MenuItem(c.getName()));
            }
        });
        // Add event handler for each Load button menu item
        loadConfigMenu.getItems().forEach(menuItem -> {
            menuItem.addEventHandler(EventType.ROOT, actionEvent -> {
                populateConfigForm(menuItem.getText());
            });
        });
        // Set default configuration
        populateConfigForm(configService.getCurrentConfig().getName());
    }

    @FXML
    private void switchToPlot() throws IOException {
        Scene priorScene = Main.getCurrentScene();
        System.out.println("config width/height:");
        System.out.println(priorScene.getWidth());
        System.out.println(priorScene.getHeight());
        Main.setRoot("plot");
        Scene currentScene = Main.getCurrentScene();
        System.out.println("plot width/height:");
        System.out.println(currentScene.getWidth());
        System.out.println(currentScene.getHeight());
        currentScene.getWindow().setHeight(685);
        currentScene.getWindow().setWidth(800);

    }

    @FXML
    private void saveAction() throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Config config = Config
                .builder()
                .name(configName.getText())
                .longitudeDegrees(longHours.getText())
                .longitudeMinutes(longMinutes.getText())
                .latitudeDegrees(latHours.getText())
                .dateOfObservation(viewDate.getValue().format(dateTimeFormatter))
                .siderealTime(siderealTime.getText())
                .horizon(horizonBox.getValue().toString())
                .plotMode(plotModeBox.getValue().toString())
                .build();
        System.out.println("Save file: " + config);
        // configService.saveConfig(config);
    }

    private void populateConfigForm(String name) {
        configs = configService.getConfigs();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // Creating an object of this class
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        DateTimeFormatter timeFormatter = builder
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .toFormatter();
        configs.getConfigList().forEach(c -> {
            if (c.getName().equalsIgnoreCase(name)) {
                // Set form field values
                configName.setText(c.getName());
                LocalDateTime localDateTime = LocalDateTime.parse(
                        c.getDateOfObservation() + " " + c.getSiderealTime(), dateTimeFormatter);
                longHours.setText(c.getLongitudeDegrees());
                longMinutes.setText(c.getLongitudeMinutes());
                latHours.setText(c.getLatitudeDegrees());
                viewDate.setValue(localDateTime.toLocalDate());
                siderealTime.setText(localDateTime.toLocalTime().format(timeFormatter));
                horizonBox.setValue(c.getHorizon());
                plotModeBox.setValue(c.getPlotMode());
                // Set current config to configuration service
                configService.setCurrentConfig(c);
            }
        });
    }

}
