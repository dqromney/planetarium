package com.dqrapps.planetarium.gui;

import com.dqrapps.planetarium.gui.splash.SplashPreloader;
import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.dqrapps.planetarium.logic.service.ConfigService;
import com.dqrapps.planetarium.logic.service.StarService;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

// HYG Database: https://github.com/InfernoEmbedded/planetarium
// https://github.com/astronexus/HYG-Database/tree/cb19d26a9910f5c0794a0dec72f29f2977eca2cc
// http://vizier.u-strasbg.fr/viz-bin/VizieR?-source=I/239/hip_main
@Getter
public class Main extends Application {

    private static Scene staticScene;
    private static final int COUNT_LIMIT = 50000;
    private ConfigService configService;
    private StarService starService;
    private Configs configs;
    private Config defaultConfig;

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

    /**
     * Application initialization.
     *
     * @See https://stackoverflow.com/questions/59957047/why-launcherimpl-launchapplication-javafx-launcher-in-the-main-class-are-lau
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        this.configService = ConfigService.getInstance();
        double progress = 25;
        notifyPreloader(new Preloader.ProgressNotification(progress));
        System.out.println(progress);
        this.starService = StarService.getInstance();
        progress += 74;
        notifyPreloader(new Preloader.ProgressNotification(progress));
        System.out.println(progress);
        showConfigAndStars();
        progress += 1;
        notifyPreloader(new Preloader.ProgressNotification(progress));
        System.out.println(progress);
    }

    private void showConfigAndStars() {
        System.out.println(configService.getCurrentConfig().toString());
        starService.getStars().getStarList().forEach(s -> System.out.println(s) );
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

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------

    public Config getDefaultConfig() {
        return defaultConfig;
    }

}
