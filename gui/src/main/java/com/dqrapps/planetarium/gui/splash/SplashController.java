package com.dqrapps.planetarium.gui.splash;

import com.dqrapps.planetarium.gui.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
// JavaFX Splash Screen video
// https://www.genuinecoder.com/javafx-splash-screen-loading-screen/#:~:text=JavaFX%20Splash%20Screen%20Splash%20screens%20are%20awesome.%20They,JavaFX%20Splash%20Screen%20%2F%20Loading%20Screen%20with%20animation.
public class SplashController implements Initializable {

    @FXML
    private static Label progress;

    public static Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println(event.getEventType().getName());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new SplashScreen().start();
        label = progress;
    }

    @FXML
    private StackPane splashScreen;

    @FXML
    private void switchToConfig() throws IOException {
        Main.setRoot("config");
    }

    class SplashScreen extends Thread {
        @Override
        public void run() {
            try {
                // Show Splash Screen for 5 seconds, then show config dialogue
                Thread.sleep(5000);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Scene scene;
                        Stage stage = new Stage();
                        try {
                            scene = new Scene(Main.loadFXML("config"));
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        splashScreen.getScene().getWindow().hide();
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//
//    }
}
