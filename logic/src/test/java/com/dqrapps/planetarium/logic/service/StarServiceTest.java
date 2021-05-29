package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Star;
import com.dqrapps.planetarium.logic.model.Stars;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class StarServiceTest {

    private StarService starService;
    private Stars stars;
    private Star star;
    private String filename;

    @Before
    public void doBefore() {
        filename = "test.json";
        starService = new StarService();
        star = Star
                .builder()
                .ra(17.12)
                .dec(-43.1)
                .mag(5.0)
                .name("ETA SCORPIO")
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
    public void saveStarTest() throws IOException {
        Stars stars = Stars.builder().starList(Arrays.asList(star)).build();
        starService.saveStars(filename, stars);
        Assert.assertTrue("file does not exist", new File(filename).exists());
    }

    @Test
    public void loadStarTest() throws IOException {
        stars = Stars.builder().starList(Arrays.asList(star)).build();
        starService.saveStars(filename, stars);

        stars = starService.loadStars(filename);
        star = stars.getStarList().get(0);

        assert (17.12 == star.getRa());
        assert (-43.1 == star.getDec());
        assert (5.0 == star.getMag());
        assert ("ETA SCORPIO".equalsIgnoreCase(star.getName()));
    }
}
