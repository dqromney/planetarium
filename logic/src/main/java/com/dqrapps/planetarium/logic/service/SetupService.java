package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Setup;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SetupService {

    ObjectMapper om = new ObjectMapper();

    public void saveSetup(String fileName, Setup setup) throws IOException {
        om.writeValue(new File(fileName), setup);
    }

    public Setup loadSetup(String fileName) throws IOException {
        return om.readValue(new File(fileName), Setup.class);
    }

    public boolean defaultSetupExists() {
        return new File("default.json").exists();
    }
}
