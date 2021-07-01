package com.dqrapps.planetarium.gui;

import com.dqrapps.planetarium.gui.splash.SplashPreloader;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

// HYG Database: https://github.com/InfernoEmbedded/planetarium
// https://github.com/astronexus/HYG-Database/tree/cb19d26a9910f5c0794a0dec72f29f2977eca2cc
// http://vizier.u-strasbg.fr/viz-bin/VizieR?-source=I/239/hip_main
public class Main extends Application {

    private static Scene scene;
    private static final int COUNT_LIMIT = 500000;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        scene = new Scene(loadFXML("splash"));
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
//        for(int i = 0; i < COUNT_LIMIT; i++) {
//            double progress = (100 * i) / COUNT_LIMIT;
//            notifyPreloader(new Preloader.ProgressNotification(progress));
//            System.out.println(progress);
//        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

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
