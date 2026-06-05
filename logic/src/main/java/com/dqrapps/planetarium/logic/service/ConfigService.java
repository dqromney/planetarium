package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Configs;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Singleton Configuration Service.
 */
public class ConfigService {

    private static final Logger log = Logger.getLogger(ConfigService.class.getName());
    private static ConfigService instance = null;
    private static final String resourceName = "/data/configs.json";
    private static final String fallbackFilename = "configs.json";

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
            // Try to save to gui subdirectory first if it exists
            File guiDir = new File("gui");
            File configFile;

            if (guiDir.exists() && guiDir.isDirectory()) {
                configFile = new File(guiDir, fallbackFilename);
            } else {
                configFile = new File(fallbackFilename);
            }

            om.writerFor(Configs.class).writeValue(configFile, configs);
            log.info("Saved configs to: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            log.warning("Error saving configs: " + e.getMessage());
        }
        return this.getConfigs();
    }

    private Configs loadConfigs() {
        if (null == this.configs) {
            try {
                // Try loading from classpath resource first
                try (InputStream is = getClass().getResourceAsStream(resourceName)) {
                    if (is != null) {
                        this.configs = om.readerFor(Configs.class).readValue(is);
                        log.info("Loaded configs from classpath resource: " + resourceName);
                        return this.configs;
                    }
                } catch (Exception e) {
                    log.info("Could not load from resource: " + e.getMessage());
                }

                // Fall back to external file with multiple location attempts
                File configFile = new File(fallbackFilename);

                // If not found in current directory, try gui subdirectory
                if (!configFile.exists()) {
                    configFile = new File("gui/" + fallbackFilename);
                    log.info("Trying alternate path: gui/" + fallbackFilename);
                }

                // If still not found, try parent directory
                if (!configFile.exists()) {
                    configFile = new File("../" + fallbackFilename);
                    log.info("Trying alternate path: ../" + fallbackFilename);
                }

                if (configFile.exists()) {
                    this.configs = om.readerFor(Configs.class).readValue(configFile);
                    log.info("Loaded configs from: " + configFile.getAbsolutePath());
                } else {
                    log.warning("Config file not found in any location. Using empty config list.");
                    this.configs = new Configs();
                    this.configs.setConfigList(new java.util.ArrayList<>());
                }
            } catch (IOException e) {
                log.warning("Error loading configs: " + e.getMessage());
                this.configs = new Configs();
                this.configs.setConfigList(new java.util.ArrayList<>());
            }
        }
        return this.configs;
    }

    private boolean defaultSetupExists() {
        // Check if resource exists in classpath
        InputStream is = getClass().getResourceAsStream(resourceName);
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore close exception
            }
            return true;
        }

        // Check if file exists in multiple locations
        File configFile = new File(fallbackFilename);
        if (configFile.exists()) {
            return true;
        }

        configFile = new File("gui/" + fallbackFilename);
        if (configFile.exists()) {
            return true;
        }

        configFile = new File("../" + fallbackFilename);
        return configFile.exists();
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
