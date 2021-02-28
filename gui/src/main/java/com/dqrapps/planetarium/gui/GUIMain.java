package com.dqrapps.planetarium.gui;

import com.dqrapps.planetarium.logic.CoolLogic;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUIMain extends Application {

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
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
        // ToggleButton Reservation = new ToggleButton();
        ToggleButton individual = new ToggleButton("Individual");
        ToggleButton continuous = new ToggleButton("Continuous");
        ToggleGroup groupPlotMode = new ToggleGroup();
        individual.setToggleGroup(groupPlotMode);
        continuous.setToggleGroup(groupPlotMode);

        //Label for technologies known
        Text technologiesLabel = new Text("Technologies Known");

        //check box for education
        CheckBox javaCheckBox = new CheckBox("Java");
        javaCheckBox.setIndeterminate(false);

        //check box for education
        CheckBox dotnetCheckBox = new CheckBox("DotNet");
        javaCheckBox.setIndeterminate(false);

        //Label for education
        Text educationLabel = new Text("Educational qualification");

        //list View for educational qualification
        ObservableList<String> names = FXCollections.observableArrayList(
                "Engineering", "MCA", "MBA", "Graduation", "MTECH", "Mphil", "Phd");
        ListView<String> educationListView = new ListView<String>(names);

        //Label for location
        Text locationLabel = new Text("location");

        //Choice box for location
        ChoiceBox locationchoiceBox = new ChoiceBox();
        locationchoiceBox.getItems().addAll
                ("Hyderabad", "Chennai", "Delhi", "Mumbai", "Vishakhapatnam");

        //Label for Save button
        Button buttonSave = new Button("Save");

        //Label for Load button
        Button buttonload = new Button("Load");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(500, 250);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(longitudeDegLabel, 0, 0);
        gridPane.add(longitudeDegText, 1, 0);
        gridPane.add(longitudeMinLabel, 2, 0);
        gridPane.add(longitudeMinText, 3, 0);

        gridPane.add(latitudeDegLabel, 0, 1);
        gridPane.add(latitudeDegText, 1, 1);

        gridPane.add(dooLabel, 0, 2);
        gridPane.add(datePicker, 1, 2);

        gridPane.add(timeLabel, 0, 3);
        gridPane.add(timeText, 1, 3);

        gridPane.add(horizonLabel, 0, 4);
        gridPane.add(northRadio, 1, 4);
        gridPane.add(southRadio, 2, 4);
        gridPane.add(plotModeLabel, 0, 5);
        gridPane.add(individual, 1, 5);
        gridPane.add(continuous, 2, 5);

//        gridPane.add(technologiesLabel, 0, 6);
//        gridPane.add(javaCheckBox, 1, 6);
//        gridPane.add(dotnetCheckBox, 2, 6);
//
//        gridPane.add(educationLabel, 0, 7);
//        gridPane.add(educationListView, 1, 7);
//
//        gridPane.add(locationLabel, 0, 8);
//        gridPane.add(locationchoiceBox, 1, 8);

        gridPane.add(buttonSave, 2, 7);
        gridPane.add(buttonload, 3, 7);

        //Styling nodes
        buttonSave.setStyle(
                "-fx-background-color: darkslateblue; -fx-textfill: white;");

        longitudeDegLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        longitudeMinLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        latitudeDegLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        dooLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        timeLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        horizonLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        plotModeLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        technologiesLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        educationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        locationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");

        //Setting the back ground color
        gridPane.setStyle("-fx-background-color: BEIGE;");

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        stage.setTitle("Planetarium Setup Menu");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

    }
}
