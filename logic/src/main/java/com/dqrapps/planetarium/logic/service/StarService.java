package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Stars;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class StarService {

    ObjectMapper om = new ObjectMapper();

    public void saveStars(String fileName, Stars stars) throws IOException {
        om.writeValue(new File(fileName), stars);
    }

    public Stars loadStars(String fileName) throws IOException {
        return om.readerFor(Stars.class).readValue(new File(fileName));
    }

    public boolean defaultStarsExists() {
        return new File("stars.json").exists();
    }
}
