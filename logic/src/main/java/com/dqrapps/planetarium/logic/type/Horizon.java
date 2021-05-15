package com.dqrapps.planetarium.logic.type;

public enum Horizon {
    NORTH("North"),
    SOUTH("South");

    private String token;

    Horizon(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
