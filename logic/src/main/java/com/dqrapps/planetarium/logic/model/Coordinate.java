package com.dqrapps.planetarium.logic.model;

public class Coordinate {
    double x, y, mag;
    String name;

    public Coordinate(double x, double y, double mag, String name) {
        this.x = x;
        this.y = y;
        this.mag = mag;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMag() {
        return mag;
    }

    public void setMag(double mag) {
        this.mag = mag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                ", mag=" + mag +
                ", name='" + name + '\'' +
                '}';
    }
}
