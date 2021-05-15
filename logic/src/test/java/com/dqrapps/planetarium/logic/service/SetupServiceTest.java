package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Setup;
import com.dqrapps.planetarium.logic.type.Horizon;
import com.dqrapps.planetarium.logic.type.PlotMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SetupServiceTest {

    private SetupService setupService;
    private Setup setup;
    private String filename;

    @Before
    public void doBefore() {
        filename = "test.json";
        setupService = new SetupService();
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        String siderealTime = String.format("%1$02d:%2$02d", now.getHour(), now.getMinute());
        setup = Setup.builder()
                .longitudeDegrees("-112")
                .longitudeMinutes("2")
                .latitudeDegrees("40")
                .dateOfObservation(now.toLocalDate().toString())
                .siderealTime(siderealTime)
                .horizon(Horizon.NORTH.getToken())
                .plotMode(PlotMode.INDIVIDUAL.getMode())
                .build();
    }

    @After
    public void cleanup() {
        var file = new File(filename);
        if (file.exists()) {
            Assert.assertTrue(file.delete());
        }
    }

    @Test
    public void saveSetupTest() throws IOException {
        setupService.saveSetup(filename, setup);
        Assert.assertTrue("file does not exist", new File(filename).exists());
    }

    @Test
    public void loadSetupTest() throws IOException {
        setupService.saveSetup(filename, setup);
        Assert.assertTrue("file does not exist", new File(filename).exists());

        setup = setupService.loadSetup(filename);
        assert ("-112".equalsIgnoreCase(setup.getLongitudeDegrees()));
        assert ("2".equalsIgnoreCase(setup.getLongitudeMinutes()));
    }
}
