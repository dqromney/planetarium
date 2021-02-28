package com.dqrapps.planetarium.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Setup {

    public Setup() {
    }

    public void doSetupMenu(Stage pStage) {
        //Label for Longitude Degrees
        Text longitudeDegLabel = new Text("Longitude (Deg)");

        //Text field for Longitude Degree
        TextField longitudeDegText = new TextField();

        //Label for Longitude Minutes
        Text longitudeMinLabel = new Text("Minutes");

        //Text field for Longitude Minutes
        TextField longitudeMinText = new TextField();

        //Label for Latitude Degrees
        Text latitudeDegLabel = new Text("Latitude (Deg)");

        //Text field for Latitude Degree
        TextField latitudeDegText = new TextField();

        //Label for date of observation
        Text dooLabel = new Text("Date of observation");

        //date picker to choose date
        DatePicker datePicker = new DatePicker();

        //Label for Sidereal time of observation
        Text timeLabel = new Text("Sidereal time");

        //Label for Sidereal time of observation
        TextField timeText = new TextField();

        //Label for horizon
        Text horizonLabel = new Text("Horizon");

        //Toggle group of radio buttons
        ToggleGroup groupHorizon = new ToggleGroup();
        RadioButton northRadio = new RadioButton("North");
        northRadio.setToggleGroup(groupHorizon);
        RadioButton southRadio = new RadioButton("South");
        southRadio.setToggleGroup(groupHorizon);

        //Label for reservation
        Text plotModeLabel = new Text("Plot Mode");

        //Toggle button for Plot Mode: Individual or Continuous
        ToggleButton individual = new ToggleButton("Individual");
        ToggleButton continuous = new ToggleButton("Continuous");
        ToggleGroup groupPlotMode = new ToggleGroup();
        individual.setToggleGroup(groupPlotMode);
        continuous.setToggleGroup(groupPlotMode);

        //Label for Save button
        Button buttonSave = new Button("Save");

        //Label for Load button
        Button buttonload = new Button("Load");

        // Label for Exit Setup menu
        Button buttonExit = new Button("Exit");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(600, 250);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.BASELINE_LEFT);

        //Arranging all the nodes in the grid
        int row = 0;
        gridPane.add(longitudeDegLabel, 0, row);
        gridPane.add(longitudeDegText, 1, row);
        gridPane.add(longitudeMinLabel, 2, row);
        gridPane.add(longitudeMinText, 3, row);

        row ++;
        gridPane.add(latitudeDegLabel, 0, row);
        gridPane.add(latitudeDegText, 1, row);

        row ++;
        gridPane.add(dooLabel, 0, row);
        gridPane.add(datePicker, 1, row);

        row ++;
        gridPane.add(timeLabel, 0, row);
        gridPane.add(timeText, 1, row);

        row ++;
        gridPane.add(horizonLabel, 0, row);
        gridPane.add(northRadio, 1, row);
        gridPane.add(southRadio, 2, row);

        row ++;
        gridPane.add(plotModeLabel, 0, row);
        gridPane.add(individual, 1, row);
        gridPane.add(continuous, 2, row);

        row += 2;;
        gridPane.add(buttonExit, 0, row);
        gridPane.add(buttonSave, 1, row);
        gridPane.add(buttonload, 2, row);

        //Styling nodes
        buttonExit.setStyle("-fx-background-color: #503B31; -fx-textfill: white;"); // Dark Liver Horses
        buttonSave.setStyle("-fx-background-color: #705D56; -fx-textfill: white;"); // Umber
        buttonload.setStyle("-fx-background-color: #9097C0; -fx-textfill: white;"); // Blue Bell

        // Font Color: Rich Black FOGRA 39
        longitudeDegLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202;");
        longitudeMinLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202;");
        latitudeDegLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
        dooLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
        timeLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
        horizonLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");
        plotModeLabel.setStyle("-fx-font: normal bold 15px 'serif'; -fx-font-color: #020202; ");

        //Setting the back ground color
        gridPane.setStyle("-fx-background-color: #A7BBEC;"); // Wild Blue Yonder

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        pStage.setTitle("Planetarium Setup Menu");

        //Adding scene to the pStage
        pStage.setScene(scene);

        //Displaying the contents of the pStage
        pStage.show();
    }

}
