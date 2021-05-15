module gui{
    requires javafx.controls;
    requires logic;
    requires static lombok;
    opens com.dqrapps.planetarium.gui to javafx.graphics;
}
