package com.dqrapps.planetarium.logic.model;

public enum Horizon {
    NORTH("Northern"),
    SOUTH("Southern"),
    UNDEFINED("Undefined");

    private String name;

    Horizon(String name) {
        this.name = name;
    }
}
