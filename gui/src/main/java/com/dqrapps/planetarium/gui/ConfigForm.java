//package com.dqrapps.planetarium.gui;
//
//import com.dqrapps.planetarium.logic.model.Config;
//import com.dqrapps.planetarium.logic.service.ConfigService;
//import com.dqrapps.planetarium.logic.type.Horizon;
//import com.dqrapps.planetarium.logic.type.PlotMode;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//
//public class ConfigForm {
//
//    private Config config;
//    private final ConfigService configService;
//
//    public ConfigForm() throws IOException {
//        configService = new ConfigService();
//        if (!configService.defaultSetupExists()) {
//            // Default setup
//            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
//            String siderealTime = String.format("%1$02d:%2$02d", now.getHour(), now.getMinute());
//            config = Config.builder()
//                    .name("default")
//                    .longitudeDegrees("-112")
//                    .longitudeMinutes("2")
//                    .latitudeDegrees("40")
//                    .dateOfObservation(now.toLocalDate().toString())
//                    .siderealTime(siderealTime)
//                    .horizon(Horizon.NORTH.getToken())
//                    .plotMode(PlotMode.INDIVIDUAL.getMode())
//                    .build();
//            configService.saveConfig("configs.json", config);
//        } else {
//            config = configService.loadConfig("configs.json");
//        }
//    }
//
//    public void doConfigForm(Stage pStage) {
//        Text configNameLabel = new Text("Name");
//        TextField configNameText = new TextField(config.getName());
//
//        Text longitudeDegLabel = new Text("Longitude (Deg)"); // DEG = -180 to +180; -112
//        TextField longitudeDegText = new TextField(config.getLongitudeDegrees());
//
//        Text longitudeMinLabel = new Text("Minutes"); // Minutes = 0-59; 2
//        TextField longitudeMinText = new TextField(config.getLongitudeMinutes()); // DEG = 0-90; 40
//
//        Text latitudeDegLabel = new Text("Latitude (Deg)");
//        TextField latitudeDegText = new TextField(config.getLatitudeDegrees());
//
//        Text dateOfObservation = new Text("Date of observation");
//        DatePicker datePicker = new DatePicker(LocalDate.parse(config.getDateOfObservation()));
//
//        Text timeLabel = new Text("Sidereal time"); // Time = 0-23, Minutes = 0-59, HH:MM
//        TextField timeText = new TextField(config.getSiderealTime());
//
//        Text horizonLabel = new Text("Horizon"); // N = North, S = South
//        ToggleGroup groupHorizon = new ToggleGroup();
//        RadioButton northRadio = new RadioButton("North");
//        northRadio.setToggleGroup(groupHorizon);
//        RadioButton southRadio = new RadioButton("South");
//        southRadio.setToggleGroup(groupHorizon);
//        if ("North".equalsIgnoreCase(config.getHorizon())) {
//            northRadio.fire();
//        } else {
//            southRadio.fire();
//        }
//        northRadio.setOnAction(actionEvent -> config.setHorizon(Horizon.NORTH.getToken()));
//        southRadio.setOnAction(actionEvent -> config.setHorizon(Horizon.SOUTH.getToken()));
//
//        Text plotModeLabel = new Text("Plot Mode");
//        ToggleButton individualBtn = new ToggleButton("Individual");
//        ToggleButton continuousBtn = new ToggleButton("Continuous");
//        ToggleGroup groupPlotMode = new ToggleGroup();
//        individualBtn.setToggleGroup(groupPlotMode);
//        continuousBtn.setToggleGroup(groupPlotMode);
//        if ("Individual".equalsIgnoreCase(config.getPlotMode())) {
//            individualBtn.fire();
//        } else {
//            continuousBtn.fire();
//        }
//        individualBtn.setOnAction(actionEvent -> config.setPlotMode(PlotMode.INDIVIDUAL.getMode()));
//        continuousBtn.setOnAction(actionEvent -> config.setPlotMode(PlotMode.CONTINUOUS.getMode()));
//
//        // Action Buttons
//        Button saveBtn = new Button("Save");
//        saveBtn.setOnAction(actionEvent -> {
//            try {
//                // Capture updated form data
//                config.setName(configNameText.getText());
//                config.setLongitudeDegrees(longitudeDegText.getText());
//                config.setLongitudeMinutes(longitudeMinText.getText());
//                config.setLatitudeDegrees(latitudeDegText.getText());
//                config.setDateOfObservation(datePicker.getValue().toString());
//                config.setSiderealTime(timeText.getText());
//                // Horizon and PlotMode are set with events
//                configService.saveConfig("configs.json", config);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        Button loadBtn = new Button("Load");
//        loadBtn.setOnAction(actionEvent -> {
//            try {
//                config = configService.loadConfig("configs.json");
//                configNameText.setText(config.getName());
//                longitudeDegText.setText(config.getLongitudeDegrees());
//                longitudeMinText.setText(config.getLongitudeMinutes());
//                latitudeDegText.setText(config.getLatitudeDegrees());
//                datePicker.setValue(LocalDate.parse(config.getDateOfObservation()));
//                timeText.setText(config.getSiderealTime());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        Button exitBtn = new Button("Exit");
//        exitBtn.setOnAction(actionEvent -> {
//            System.exit(0);
//        });
//
//        //Creating a Grid Pane
//        GridPane gridPane = new GridPane();
//
//        //Setting size for the pane
//        gridPane.setMinSize(600, 250);
//
//        //Setting the padding
//        gridPane.setPadding(new Insets(10, 10, 10, 10));
//
//        //Setting the vertical and horizontal gaps between the columns
//        gridPane.setVgap(5);
//        gridPane.setHgap(5);
//
//        //Setting the Grid alignment
//        gridPane.setAlignment(Pos.BASELINE_LEFT);
//
//        //Arranging all the nodes in the grid
//        var row = 0;
//        gridPane.add(configNameLabel, 0, row);
//        gridPane.add(configNameText, 1, row);
//        gridPane.add(longitudeDegLabel, 0, ++row);
//        gridPane.add(longitudeDegText, 1, row);
//        gridPane.add(longitudeMinLabel, 2, row);
//        gridPane.add(longitudeMinText, 3, row);
//
//        gridPane.add(latitudeDegLabel, 0, ++row);
//        gridPane.add(latitudeDegText, 1, row);
//
//        gridPane.add(dateOfObservation, 0, ++row);
//        gridPane.add(datePicker, 1, row);
//
//        gridPane.add(timeLabel, 0, ++row);
//        gridPane.add(timeText, 1, row);
//
//        gridPane.add(horizonLabel, 0, ++row);
//        gridPane.add(northRadio, 1, row);
//        gridPane.add(southRadio, 2, row);
//
//        gridPane.add(plotModeLabel, 0, ++row);
//        gridPane.add(individualBtn, 1, row);
//        gridPane.add(continuousBtn, 2, row);
//
//        gridPane.add(exitBtn, 0, row += 2);
//        gridPane.add(saveBtn, 1, row);
//        gridPane.add(loadBtn, 2, row);
//
//        //Styling nodes
////        buttonExit.setStyle("-fx-background-color: #503B31; -fx-textfill: white;"); // Dark Liver Horses
////        buttonSave.setStyle("-fx-background-color: #705D56; -fx-textfill: white;"); // Umber
////        buttonload.setStyle("-fx-background-color: #9097C0; -fx-textfill: white;"); // Blue Bell
//        exitBtn.setStyle("-fx-textfill: white;"); // Dark Liver Horses
//        saveBtn.setStyle("-fx-textfill: white;"); // Umber
//        loadBtn.setStyle("-fx-textfill: white;"); // Blue Bell
//
//        // Font Color: Rich Black FOGRA 39
//        longitudeDegLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202;");
//        longitudeMinLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202;");
//        latitudeDegLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
//        dateOfObservation.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
//        timeLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
//        horizonLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
//        plotModeLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
//
//        //Setting the back ground color
//        gridPane.setStyle("-fx-background-color: #A7BBEC;"); // Wild Blue Yonder
//
//        //Creating a scene object
//        var scene = new Scene(gridPane);
//
//        //Setting title to the Stage
//        pStage.setTitle("Planetarium SetupForm Menu");
//
//        //Adding scene to the pStage
//        pStage.setScene(scene);
//
//        //Displaying the contents of the pStage
//        pStage.centerOnScreen();
//    }
//
//}
