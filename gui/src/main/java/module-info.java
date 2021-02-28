module gui{
    requires logic;
    requires javafx.controls;
    opens com.dqrapps.planetarium.gui to javafx.graphics;
}