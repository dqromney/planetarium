package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Constellations;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service for loading constellation data.
 */
public class ConstellationService {

    private static ConstellationService instance = null;
    private static final String resourceName = "/data/constellations.json";
    private static final String fallbackFilename = "constellations.json";

    private ObjectMapper om;
    private Constellations constellations;

    private ConstellationService() {
        om = new ObjectMapper();
    }

    @SneakyThrows
    public static ConstellationService getInstance() {
        if (instance == null) {
            instance = new ConstellationService();
            instance.constellations = instance.loadConstellations(null);
        }
        return instance;
    }

    public Constellations loadConstellations(String fileName) throws IOException {
        if (fileName == null) {
            // Try loading from classpath resource first
            try (InputStream is = getClass().getResourceAsStream(resourceName)) {
                if (is != null) {
                    return om.readerFor(Constellations.class).readValue(is);
                }
            } catch (Exception e) {
                System.out.println("Could not load from resource: " + e.getMessage());
            }

            // Fall back to external file
            fileName = fallbackFilename;
        }

        File file = new File(fileName);
        if (file.exists()) {
            return om.readerFor(Constellations.class).readValue(file);
        }

        // Return empty if not found
        System.out.println("No constellation data found");
        return new Constellations();
    }

    public Constellations getConstellations() {
        return constellations;
    }
}

