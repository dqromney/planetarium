package com.dqrapps.planetarium.gui.config;

import com.dqrapps.planetarium.gui.Main;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.type.Horizon;
import com.dqrapps.planetarium.logic.type.PlotMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.LocalDate;
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
    private TextField longHours;
    @FXML
    private TextField longMinutes;
    @FXML
    private TextField latHours;
    @FXML
    private DatePicker viewDate;
    @FXML
    private TextField siderealTime;

    private Config config;

    @SneakyThrows
    @FXML
    private void initialize() {
        LocalDateTime localDateTime = LocalDateTime.now();
        configService = new ConfigService();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern ( "yyyy-MM-dd HH:mm" );
        // Creating an object of this class
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        DateTimeFormatter formatter = builder
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .toFormatter();

        horizonBox.setValue(Horizon.NORTH.getToken());
        horizonBox.setItems(horizonList);
        plotModeBox.setValue(PlotMode.INDIVIDUAL.getMode());
        plotModeBox.setItems(plotModeList);
        if (configService.defaultSetupExists()) {
            this.config = configService.loadConfig(null); // Get Default filename
            localDateTime = LocalDateTime.parse(
                    config.getDateOfObservation() + " " + config.getSiderealTime(), dateTimeFormatter);
        }
        longHours.setText(config.getLongitudeDegrees());
        longMinutes.setText(config.getLongitudeMinutes());
        latHours.setText(config.getLatitudeDegrees());
        viewDate.setValue(localDateTime.toLocalDate());
        System.out.println(localDateTime.toLocalTime().format(formatter));
        siderealTime.setText(localDateTime.toLocalTime().format(formatter));
        horizonBox.setValue(config.getHorizon());
        plotModeBox.setValue(config.getPlotMode());
    }

    @FXML
    private void switchToPlot() throws IOException {
        Main.setRoot("plot");
    }


}
