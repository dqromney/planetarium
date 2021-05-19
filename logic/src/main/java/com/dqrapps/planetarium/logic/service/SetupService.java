package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Config;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SetupService {

    ObjectMapper om = new ObjectMapper();

    public void saveConfig(String fileName, Config setup) throws IOException {
        om.writeValue(new File(fileName), setup);
    }

    public Config loadConfig(String fileName) throws IOException {
        return om.readValue(new File(fileName), Config.class);
    }

    public boolean defaultSetupExists() {
        return new File("default.json").exists();
    }
}
