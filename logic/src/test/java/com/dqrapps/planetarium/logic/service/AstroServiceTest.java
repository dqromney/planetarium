package com.dqrapps.planetarium.logic.service;

import org.junit.Before;
import org.junit.Test;

public class AstroServiceTest {

    private AstroService astroService;

    @Before
    public void doBefore() {
        astroService = new AstroService();
    }

    @Test
    public void toDegreesTest() {
        double degrees = astroService.toDegrees(-112.0);
        assert(degrees == -1.9547687622336491);

        degrees = astroService.toDegrees(40.0);
        assert(degrees == 0.6981317007977318);
    }

    @Test
    public void isVisibleTest() {
        double latDeg;
        int visible = 0;
        int nonVisible = 0;
        for(latDeg = -180.0; latDeg <= 180.0; latDeg = incDegree(latDeg) ) {
            if (astroService.isVisible(40.0, latDeg) == true) {
                System.out.println("LatDeg is visible:" + latDeg);
                visible ++;
            } else {
                System.out.println("LatDeg is NOT visible:" + latDeg);
                nonVisible ++;
            }
        }
        System.out.println("Visible: "+ visible + "  Non-Visible: " +  nonVisible);
    }

    private double incDegree(double degree) {
        return degree + 1.0;
    }
}
