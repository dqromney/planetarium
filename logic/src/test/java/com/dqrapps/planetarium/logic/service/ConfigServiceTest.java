package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Config;
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

public class ConfigServiceTest {

    private ConfigService configService;
    private Config config;
    private String filename;

    @Before
    public void doBefore() {
        filename = "test.json";
        configService = new ConfigService();
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        String siderealTime = String.format("%1$02d:%2$02d", now.getHour(), now.getMinute());
        config = Config.builder()
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
    public void saveConfigTest() throws IOException {
        configService.saveConfig(filename, config);
        Assert.assertTrue("file does not exist", new File(filename).exists());
    }

    @Test
    public void loadConfigTest() throws IOException {
        configService.saveConfig(filename, config);
        Assert.assertTrue("file does not exist", new File(filename).exists());

        config = configService.loadConfig(filename);
        assert ("-112".equalsIgnoreCase(config.getLongitudeDegrees()));
        assert ("2".equalsIgnoreCase(config.getLongitudeMinutes()));
    }
}
