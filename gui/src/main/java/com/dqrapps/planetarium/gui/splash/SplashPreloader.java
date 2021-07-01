package com.dqrapps.planetarium.gui.splash;

import com.dqrapps.planetarium.gui.Main;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashPreloader extends Preloader {

    private Stage preLoaderStage;
    private Scene scene;

    public SplashPreloader() {
    }

    @Override
    public void init() throws Exception {
        // Parent root1 = FXMLLoader.load(getClass().getResource("splash.fxml"));
        scene = new Scene(Main.loadFXML("splash"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preLoaderStage = primaryStage;

        // Set preloader scene and show stage
        preLoaderStage.setScene(scene);
        preLoaderStage.initStyle(StageStyle.UNDECORATED); // No resize, exit, etc buttons on screen
        preLoaderStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        // Handle application notification at this point
        if (info instanceof ProgressNotification) {
            SplashController.label.setText("Loading" + ((ProgressNotification) info).getProgress() + "%");
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_START:
                // Called after App#init and before App#start is called.
                System.out.println("BEFORE START");
                preLoaderStage.hide();
                break;
        }
    }
}
