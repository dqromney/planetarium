package com.dqrapps.planetarium.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// HYG Database: https://github.com/InfernoEmbedded/planetarium
// https://github.com/astronexus/HYG-Database/tree/cb19d26a9910f5c0794a0dec72f29f2977eca2cc
// http://vizier.u-strasbg.fr/viz-bin/VizieR?-source=I/239/hip_main
public class App extends Application {

    private static Scene scene;

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("splash"));
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }


//    @Override
//    public void start(Stage stage) throws Exception {
//
//        Splash splash = new Splash();
//        splash.doSplash(stage);
//
//        // StartUp startup = new Startup();
//        // startup.doStartup();
//
//        // SetupForm menu; edit existing or setup new observation
////        ConfigForm configForm = new ConfigForm();
////        configForm.doConfigForm(stage);
//
//        CalculateAndPlot calculateAndPlot = new CalculateAndPlot();
//        calculateAndPlot.show();
//        calculateAndPlot.doStarChart(stage);
//
//    }
}
