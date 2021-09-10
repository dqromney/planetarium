package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Coordinate;
import com.dqrapps.planetarium.logic.model.Horizon;
import com.dqrapps.planetarium.logic.model.Screen;
import com.dqrapps.planetarium.logic.model.Stars;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AstroServiceTest {

    private AstroService astroService;
    private ConfigService configService;
    private Stars stars = new Stars();

    @Before
    public void doBefore() throws IOException {
        astroService = new AstroService(new Screen(800, 600));
        stars = StarService.getInstance().getStars();
        configService = ConfigService.getInstance();
    }

    @Test
    public void getCoordinateTest() {
        Horizon horizon = Horizon.valueOf(configService.getCurrentConfig().getHorizon().toUpperCase());
        stars.getStarList().forEach(s -> {
            Coordinate coordinate = astroService.getCoordinate(horizon, configService.getCurrentConfig(), s);
            if (coordinate != null) {
                System.out.println(coordinate);
            }
        });
    }

    @Test
    public void fromHMStoDegreesTest() {
        double hours = -112.0;
        double minutes = 2.0;
        double seconds = 20.6592;
        double degrees = astroService.fromHMStoDegrees(hours, minutes, seconds);
        assert(degrees == -112.039072);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------------------------------------------------
    private double incDegree(double degree) {
        return degree + 1.0;
    }
}