/**
 * Graphical User Interface or GUI.
 */
module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires logic;
    requires static lombok;
    requires java.logging;
    requires com.fasterxml.jackson.databind;

    opens com.dqrapps.planetarium.gui to javafx.fxml, javafx.graphics;

    exports com.dqrapps.planetarium.gui;
    exports com.dqrapps.planetarium.gui.config;
    opens com.dqrapps.planetarium.gui.config to javafx.fxml, javafx.graphics;
    exports com.dqrapps.planetarium.gui.splash;
    opens com.dqrapps.planetarium.gui.splash to javafx.fxml, javafx.graphics;
    exports com.dqrapps.planetarium.gui.plot;
    opens com.dqrapps.planetarium.gui.plot to javafx.fxml, javafx.graphics;
}
