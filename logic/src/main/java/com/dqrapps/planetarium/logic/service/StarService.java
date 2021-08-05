package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Stars;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;

public class StarService {

    private ObjectMapper om;
    private Stars stars;

    private static StarService instance = null;
    private static final String defaultStarsFilename = "stars.json";

    private StarService() {
        om = new ObjectMapper();
    }

    @SneakyThrows
    public static StarService getInstance() {
        if (instance == null) {
            instance = new StarService();
            instance.stars = instance.loadStars(null);
        }
        return instance;
    }

    public void saveStars(String fileName, Stars stars) throws IOException {
        om.writeValue(new File(fileName), stars);
    }

    public Stars loadStars(String fileName) throws IOException {
        if (null == fileName) {
            fileName = defaultStarsFilename;
        }
        return om.readerFor(Stars.class).readValue(new File(fileName));
    }

    public boolean defaultStarsExists() {
        return new File(defaultStarsFilename).exists();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------

    public Stars getStars() {
        return stars;
    }

    public void setStars(Stars stars) {
        this.stars = stars;
    }
}
