package com.dqrapps.planetarium.gui;

import com.dqrapps.planetarium.gui.splash.SplashPreloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

// HYG Database: https://github.com/InfernoEmbedded/planetarium
// https://github.com/astronexus/HYG-Database/tree/cb19d26a9910f5c0794a0dec72f29f2977eca2cc
// http://vizier.u-strasbg.fr/viz-bin/VizieR?-source=I/239/hip_main
public class Main extends Application {

    private static Scene staticScene;
    private static final int COUNT_LIMIT = 50000;

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashPreloader.class.getCanonicalName());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            exit(primaryStage);}
        );
        doSplashScreen(primaryStage);
    }

    @Override
    public void init() throws Exception {
        for(int i = 0; i < COUNT_LIMIT; i++) {
            double progress = (100 * i) / COUNT_LIMIT;
            // https://stackoverflow.com/questions/59957047/why-launcherimpl-launchapplication-javafx-launcher-in-the-main-class-are-lau
            notifyPreloader(new Preloader.ProgressNotification(progress));
            System.out.println(progress);
        }
    }

    private void doSplashScreen(Stage primaryStage) throws IOException {
        staticScene = new Scene(loadFXML("plot"));
        staticScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        // primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(staticScene);
        primaryStage.show();
    }

    private void exit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exit application");
        alert.setContentText("Do you want to exit application?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Exiting application.");
            stage.close();
        }
    }

    // TODO Move these into a utility
    public static void setRoot(String fxml) throws IOException {
        // Your FXML defines the root to be an AnchorPane (and you can't set the root twice, which is why you are getting the error).
        staticScene.setRoot(loadFXML(fxml));
    }

    public static Scene getCurrentScene() {
        return staticScene;
    }

    // TODO Move these into a utility
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
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
