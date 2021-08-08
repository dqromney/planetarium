package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
                instance.getConfigs();
                instance.currentConfig = instance.read(defaultConfig);
            }
        }
        return instance;
    }

    public boolean save(Config config) {
        boolean result = false;
        if (doesConfigExist(config.getName())) {
            this.update(config);
            result = true;
        } else {
            result = this.create(config);
        }
        return result;
    }

    public boolean create(Config config) {
        boolean result = false;
        if (!doesConfigExist(config.getName())) {
            this.configs.getConfigList().add(config);
            this.configs = this.saveConfigs();
            this.saveConfigs();
            result = true;
        } else {
            // log.info(String.format("Config [%1$s] already exists", config.getName()));
            System.out.println(String.format("Config [%1$s] already exists", config.getName()));
        }
        return result;
    }

    public Config read(String name) {
        this.configs.getConfigList().forEach(c -> {
            if (c.getName().equalsIgnoreCase(name)) {
                currentConfig = c;
            }
        });
        return currentConfig;
    }

    public void update(Config config) {
        this.configs.getConfigList().forEach(c -> {
            if (c.getName().equalsIgnoreCase(config.getName())) {
                deepCopy(config, c);
                currentConfig = c;
                this.configs = this.saveConfigs();
            }
        });
    }

    public boolean delete(Config config) {
        if (doesConfigExist(config.getName())) {
            this.configs.getConfigList().remove(config);
            this.configs = this.saveConfigs();
        }
        return true;
    }

    public Configs getConfigs() {
        return this.loadConfigs();
    }

    public List<String> getConfigNameList() {
        this.getConfigs();
        return configs.getConfigList().stream()
                .map(c -> c.getName())
                .collect(Collectors.toList());
    }

    public boolean doesConfigExist(String name) {
        AtomicBoolean exist = new AtomicBoolean(false);
        this.configs.getConfigList().forEach(c -> {
            if (c.getName().equalsIgnoreCase(name)) {
                exist.set(true);
            }
        });
        return exist.get();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------------------------------------------------


    private Configs saveConfigs() {
        try {
            om.writerFor(Configs.class).writeValue(new File(defaultFileName), configs);
        } catch (IOException e) {
            System.out.println("Error saving configs");
            // log.throwing(this.getClass().getName(), "saveConfigs", e);
        }
        return this.getConfigs();
    }

    private Configs loadConfigs() {
        if (null == this.configs) {
            try {
                this.configs = om.readerFor(Configs.class).readValue(new File(defaultFileName));
            } catch (IOException e) {
                System.out.println("Error loading configs");
                // log.throwing(this.getClass().getName(), "loadConfigs", e);
            }
        }
        return this.configs;
    }

    private boolean defaultSetupExists() {
        return new File(defaultFileName).exists();
    }

    private void deepCopy(Config from, Config to) {
        to.setName(from.getName());
        to.setLongitudeDegrees(from.getLongitudeDegrees());
        to.setLongitudeMinutes(from.getLongitudeMinutes());
        to.setLatitudeDegrees(from.getLatitudeDegrees());
        to.setDateOfObservation(from.getDateOfObservation());
        to.setSiderealTime(from.getSiderealTime());
        to.setHorizon(from.getHorizon());
        to.setPlotMode(from.getPlotMode());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------

    public Config getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(Config currentConfig) {
        this.currentConfig = currentConfig;
    }
}
