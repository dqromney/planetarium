package com.dqrapps.planetarium.logic.model;

public enum Hemisphere {
    NORTH("Northern"),
    SOUTH("Southern"),
    UNDEFINED("Undefined");

    private String name;

    Hemisphere(String name) {
        this.name = name;
    }
}
