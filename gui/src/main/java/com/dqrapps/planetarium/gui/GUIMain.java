package com.dqrapps.planetarium.gui;

import javafx.application.Application;
import javafx.stage.Stage;

// HYG Database: https://github.com/InfernoEmbedded/planetarium
// https://github.com/astronexus/HYG-Database/tree/cb19d26a9910f5c0794a0dec72f29f2977eca2cc
// http://vizier.u-strasbg.fr/viz-bin/VizieR?-source=I/239/hip_main
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
