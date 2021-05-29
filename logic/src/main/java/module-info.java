module logic{
    requires static lombok;
    // requires static com.mhuss.AstroLib.Astro;
    requires com.fasterxml.jackson.databind;
    requires AstroLib;
    // requires com.mhuss.AstroLib;
    exports com.dqrapps.planetarium.logic.model;
    exports com.dqrapps.planetarium.logic.service;
    exports com.dqrapps.planetarium.logic.type;
}
