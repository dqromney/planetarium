package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton Configuration Service.
 */
public class ConfigService {

    private static ConfigService instance = null;
    private static final String defaultFileName = "configs.json";
    private static final String defaultConfig = "default";

    ObjectMapper om;
    Configs configs;
    Config currentConfig;

    private ConfigService() {
        om = new ObjectMapper();
    }

    public static ConfigService getInstance() throws IOException {
        if (null == instance) {
            instance = new ConfigService();
            if (instance.defaultSetupExists()) {
                instance.loadConfigs(defaultFileName);
                instance.currentConfig = instance.loadConfig(defaultConfig);
            }
        }
        return instance;
    }

    // TODO Refactor for config(s)
    public void saveConfig(String fileName, Config config) throws IOException {
        om.writerFor(Config.class).writeValue(new File(fileName), config);
    }

    public void loadConfigs(String fileName) throws IOException {
        if (fileName == null) {
            fileName = defaultFileName;
        }
        this.configs = om.readerFor(Configs.class).readValue(new File(fileName));
    }

    public Config loadConfig(String configName) throws IOException {
        if (null == configs) {
            this.loadConfigs(null);
        }
        configs.getConfigList().forEach(c -> {
            if (c.getName().equalsIgnoreCase(configName)) {
                currentConfig = c;
            }
        });
        return currentConfig;
    }

    @SneakyThrows
    public List<String> configList(String fileName) {
        if (fileName == null) {
            fileName = defaultFileName;
        }
        this.loadConfigs(fileName);
        return configs.getConfigList().stream()
                .map(c -> c.getName())
                .collect(Collectors.toList());
    }


    public boolean defaultSetupExists() {
        return new File(defaultFileName).exists();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------

    public Configs getConfigs() {
        return configs;
    }

    public Config getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(Config currentConfig) {
        this.currentConfig = currentConfig;
    }
}
