package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Config;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ConfigService {

    ObjectMapper om = new ObjectMapper();
    ClassLoader classLoader = getClass().getClassLoader();


    public void saveConfig(String fileName, Config config) throws IOException {
        om.writerFor(Config.class).writeValue(new File(fileName), config);
    }

    public Config loadConfig(String fileName) throws IOException {
        String defaultFilename = "configs.json";
        if (fileName == null) {
            fileName = defaultFilename;
        }
        return om.readerFor(Config.class).readValue(new File(fileName));
    }

    public boolean defaultSetupExists() {
        return new File("configs.json").exists();
    }
}
