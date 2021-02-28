package com.dqrapps.planetarium.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class GUIMain extends Application {

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Setup setupMenu = new Setup();
        setupMenu.doSetupMenu(stage);
    }
}
