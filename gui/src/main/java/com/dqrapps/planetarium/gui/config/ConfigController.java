package com.dqrapps.planetarium.gui.config;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.type.Horizon;
import com.dqrapps.planetarium.logic.type.PlotMode;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

@Log
public class ConfigController {

    //private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    ConfigService configService;

    @FXML
    private MenuButton loadConfigMenu;

    @FXML
    private TextField longHours;  // Now stores decimal degrees (e.g., -112.03884)
    @FXML
    private TextField latHours;   // Now stores decimal degrees (e.g., 40.68329)
    @FXML
    private DatePicker viewDate;
    @FXML
    private TextField siderealTime;
    @FXML
    private TextField configName;

    // Display preference checkboxes
    @FXML
    private CheckBox showConstellationsCheckbox;
    @FXML
    private CheckBox showGridCheckbox;
    @FXML
    private CheckBox showDSOCheckbox;
    @FXML
    private CheckBox showPlanetsCheckbox;

    private Configs configs;

    private DateTimeFormatter timeFormatter;

    /**
     * Initialize page each time it is opened.
     */
    @SneakyThrows
    @FXML
    private void initialize() {
        this.configService = ConfigService.getInstance();

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        timeFormatter = builder
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .toFormatter();


        updateConfigs();

        // Set default configuration
        populateConfigForm(configService.getCurrentConfig().getName());

        // Initialize display preferences from singleton
        DisplayPreferences prefs = DisplayPreferences.getInstance();
        if (showConstellationsCheckbox != null) {
            showConstellationsCheckbox.setSelected(prefs.isShowConstellations());
            showConstellationsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                prefs.setShowConstellations(newVal);
            });
        }
        if (showGridCheckbox != null) {
            showGridCheckbox.setSelected(prefs.isShowGrid());
            showGridCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                prefs.setShowGrid(newVal);
            });
        }
        if (showDSOCheckbox != null) {
            showDSOCheckbox.setSelected(prefs.isShowDSO());
            showDSOCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                prefs.setShowDSO(newVal);
            });
        }
        if (showPlanetsCheckbox != null) {
            showPlanetsCheckbox.setSelected(prefs.isShowPlanets());
            showPlanetsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                prefs.setShowPlanets(newVal);
            });
        }
    }

    @FXML
    private void switchToPlot() throws IOException {
        Scene priorScene = Main.getCurrentScene();
        log.info(String.format("config width/height: %1$.0f/%2$.0f", priorScene.getWidth(), priorScene.getHeight()));
        Main.setRoot("plot");
        Scene currentScene = Main.getCurrentScene();
        log.info(String.format("plot width/height: %1$.0f/%2$.0f", currentScene.getWidth(), currentScene.getHeight()));
        currentScene.getWindow().setHeight(682.0);
        currentScene.getWindow().setWidth(882.0);
    }

    @FXML
    private void saveAction() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        configName.setEditable(false);
        Config config = Config
                .builder()
                .name(configName.getText())
                .longitudeDegrees(longHours.getText())  // Decimal degrees (e.g., -112.03884)
                .longitudeMinutes("0")  // Set to "0" for backward compatibility
                .latitudeDegrees(latHours.getText())  // Decimal degrees (e.g., 40.68329)
                .dateOfObservation(viewDate.getValue().format(dateTimeFormatter))
                .siderealTime(siderealTime.getText())
                .horizon(Horizon.NORTH.getToken())  // Default to North
                .plotMode(PlotMode.INDIVIDUAL.getMode())  // Default to Individual
                .build();
        boolean doesExist = configService.doesConfigExist(config.getName());
        if (configService.save(config)) {
            log.info("Save file: " + config);
            if (!doesExist) {
                loadConfigMenu.getItems().add(new MenuItem(config.getName()));
            }
            // Show success feedback
            showSaveSuccess();
        } else {
            log.info("Error saving config");
            showSaveError();
        }
    }

    private void showSaveSuccess() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Configuration Saved");
        alert.setHeaderText(null);
        alert.setContentText("Configuration '" + configName.getText() + "' saved successfully!");
        alert.showAndWait();
    }

    private void showSaveError() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Save Failed");
        alert.setHeaderText("Failed to save configuration");
        alert.setContentText("Please check that all required fields are filled in correctly.");
        alert.showAndWait();
    }

    @FXML
    private void deleteAction() {
        Config config = configService.getCurrentConfig();
        configService.delete(config);
    }

    @FXML
    private void loadAction() {
        loadConfigMenu.getItems().clear();
        updateConfigs();
    }

    @FXML
    private void clearConfigForm() {
        LocalDateTime localDateTime = LocalDateTime.now();
        configName.setEditable(true);
        configName.setText("");
        longHours.setText("");  // Decimal longitude
        latHours.setText("");   // Decimal latitude
        viewDate.setValue(localDateTime.toLocalDate());
        siderealTime.setText(localDateTime.toLocalTime().format(timeFormatter));
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
                longHours.setText(c.getLongitudeDegrees());  // Load decimal longitude
                latHours.setText(c.getLatitudeDegrees());    // Load decimal latitude
                viewDate.setValue(localDateTime.toLocalDate());
                siderealTime.setText(localDateTime.toLocalTime().format(timeFormatter));
                // Note: horizon and plotMode are no longer displayed but are preserved in config
                // Set current config to configuration service
                configService.setCurrentConfig(c);
            }
        });
    }

    private void updateConfigs() {
        this.configs = configService.getConfigs();
        // Populate Load Button Menu items
        configs.getConfigList().forEach(c -> {
            //if (!c.getName().equalsIgnoreCase("Default")) {
                loadConfigMenu.getItems().add(new MenuItem(c.getName()));
            //}
        });
        // Add event handler for each Load button menu item
        loadConfigMenu.getItems().forEach(menuItem -> {
            menuItem.addEventHandler(EventType.ROOT, actionEvent -> {
                populateConfigForm(menuItem.getText());
            });
        });
    }

}
