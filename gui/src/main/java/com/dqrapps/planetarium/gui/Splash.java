package com.dqrapps.planetarium.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Splash {

    public void doSplash(Stage pStage) {

        // Label title
        Text planetariumLabel = new Text("Planetarium");

        // Label to Exit Splash Screen
        Button anyKey = new Button("Press Any Key");
        anyKey.setOnAction(actionEvent -> pStage.close());

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(800, 600);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.BASELINE_LEFT);

        //Arranging all the nodes in the grid
        int row = 0;
        gridPane.add(planetariumLabel, 0, row);

        gridPane.add(anyKey, 0, row += 2);

        //Styling nodes
        anyKey.setStyle("-fx-textfill: white;");

        // Font Color: Rich Black FOGRA 39
        planetariumLabel.setStyle("-fx-font: normal bold 46px 'serif'; -fx-font-color: #020202;");

        //Setting the back ground color
        gridPane.setStyle("-fx-background-color: #A7BBEC;"); // Wild Blue Yonder

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        pStage.setTitle("Planetarium SetupForm Menu");

        //Adding scene to the pStage
        pStage.setScene(scene);

        //Displaying the contents of the pStage
        pStage.show();

    }
}
