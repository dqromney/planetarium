module logic{
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    exports com.dqrapps.planetarium.logic.model;
    exports com.dqrapps.planetarium.logic.service;
    exports com.dqrapps.planetarium.logic.type;
}
