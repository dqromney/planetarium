package com.dqrapps.planetarium.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class GUIMain extends Application {

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Splash splash = new Splash();
        // splash.show();

        // StartUp startup = new Startup();
        // startup.doStartup();

        // CalculateAndPlot calculateAndPlot = new CalclateAndPlot();
        // calculateAndPlot.show();

        // Setup menu; edit existing or setup new observation
        Setup setupMenu = new Setup();
        setupMenu.doSetupMenu(stage);

    }
}
