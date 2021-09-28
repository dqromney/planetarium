package com.dqrapps.planetarium.logic.service;

//import static com.mhuss.AstroLib.Astro.DAYS_PER_HOUR;

import com.dqrapps.planetarium.logic.model.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class AstroService {
    private static double THREE_SIXTY = 360.0;
    private static double ONE_EIGHTY = THREE_SIXTY / 2.0;
    private static double NINTY = ONE_EIGHTY / 2.0;
    private static double FORTY_FIVE = NINTY / 2.0;
    private static double HALF_PI = Math.PI / 2.0;;
    private static double TWELVE_HOURS = 12.0;
    private static double NEGATIVE_ONE = -1.0;
    private static double DAYS_PER_HOUR = 1.0 / 24.0;
    private static double SIXTY = 60.0;
    private static double THIRTY_SIX_HUNDRED = 3600.0;
    private static double SCREEN_RATIO = 279.0 / 191.0;
    /*
        1.78   5.00       x(1.78)  5.00            5.0 x 191
        ---- = ----  ==>  ------ = -----   ==> x = --------- = 536.516854
        191     x           191      1               1.78
     */
    private static double X_FACTOR = 536.5; //450; //(800.0/SCREEN_RATIO); // 160.0
    private static double Y_FACTOR = X_FACTOR * (140.0 / 160.0); //(600.0/SCREEN_RATIO); // 140.0 This may not work for south
    // 600/X = 1.28 -> 600/1.28 = X
    // Original 1.78 Seems that is shows more of the upper part of the sky. The higher the number the less of the horizon is
    // shown, the lower the more is shown.
    private static double VIEW_HORIZON_HEIGHT_RATIO = 5.75;
    private static double SOUTHERN_DEFAULT_FACTOR = 3.28; //3.50;
    private static double SOUTHERN_MAGIC_NUMBER_VERTICAL = 1.73 * SOUTHERN_DEFAULT_FACTOR; // 7.50; // 1.73;
    private static double SOUTHERN_MAGIC_NUMBER_HORIZONTAL = 23.33 * SOUTHERN_DEFAULT_FACTOR; // 23.33 * 2.75; // 23.33;
    /*
        191   600      w(191)      600          279*600
        --- = ---  ==> ------ = ------- ==> w = ------- = 876.439 Width, 600 Height Canvas
        279    w        279        1              191
     */
    private Screen screen;
    private LocalDateTime now;
    private double southernDefaultFactor = SOUTHERN_DEFAULT_FACTOR;

/*
Legend:
XP - X-Polar: Right Ascension or RA(I) where I is the index for the stars (Radians: 1 degree = 0.01745329252)
YP - Y-Polar: Declination or DEC(I) where I is the index for the stars (I think Degrees) -90 to 90
MG(I) = MAG(I) or Star magnitude
RC - Northern (1) or Southern (0) horizon
LST - Current time expressed as HR + (MIN / 60) + (SEC / 1800)
HZ$ - Input horizon, i.e. 'N' or 'S'.
W = 265
LNH - Longitude Hours, i.e. 71 (-180 to 180)
LGM - Longitude Minutes (degrees), i.e. 3 min
LT = Latitude, i.e. 42 degree (0-90)
HR = Sidereal Hour
MIN = Sidereal Minute
NS = Number of Stars
H = Hour angle in radians
--- INPUT FORM ---
LH = Longitude (DEG -180 to +180), set to LNH
LM = Longitude Minutes (MIN 0 to 59), set to LGH
LT = Latitude (DEG 0 - 90)
*/

    public AstroService(Screen screen) {
        this.screen = screen;
    }

    // Apple II Plus - Plot hires point/line (x=0...279, y=0...191)
    public Coordinate getCoordinate(Horizon horizon, Config config, Star star) {
        Coordinate coordinate;
        if (Horizon.NORTH.equals(horizon)) {
            coordinate = getNorthernCoordinate(config, star);
        } else {
            coordinate = getSouthernCoordinate(config, star);
        }
        return coordinate;
    }

    // Line 780
    private Coordinate getSouthernCoordinate(Config config, Star star) {
        now = LocalDateTime.now();
        String[] siderealTimeArray = config.getSiderealTime().split(":");
        double yp = star.getDec();
        double lt = Double.valueOf(config.getLatitudeDegrees());
        // double lst = Double.valueOf(siderealTimeArray[0]) + Double.valueOf(siderealTimeArray[1]) / SIXTY;
        // LST = HR + (MIN / 60) + (SEC / 1800)
        double lst = Double.valueOf(now.getLong(ChronoField.CLOCK_HOUR_OF_DAY)) +
                Double.valueOf(now.getLong(ChronoField.MINUTE_OF_HOUR) / SIXTY) +
                Double.valueOf(now.getLong(ChronoField.SECOND_OF_MINUTE) / 1800.0) / 60.0;
        double xp = star.getRa();

        if ((yp > lt)) {
            return null;
        }
        if ((yp < (lt - NINTY))) {
            return null;
        }
        if (this.hasRisen(xp, yp, lt, lst)) {
            return null;
        }
        // XP = LST - XP: IF XP <  - 12 THEN XP = XP + 24
        xp = lst - xp;
        if (xp < -12.0) {
            xp = xp + 24.0;
        }
        if (xp > 12.0) {
            xp = xp - 24.0;
        }
        // Allows 12 Hours of Right Ascension
        // xp = 140 + xp * 23.33;
        xp = Y_FACTOR + xp * (23.33 * this.southernDefaultFactor);
        if (xp > screen.getWidth() || xp < 0.0) {
        //if (xp > X_FACTOR || xp < 0.0) {
            return null;
        }
        // Allow 90 Degree of Declination
        // yp = 1.73 * (lt - yp);
        yp = (1.73 * this.southernDefaultFactor) * (lt - yp);
        // if (yp < 0 || yp > 159) {
        if (yp < 0 || yp > X_FACTOR-1) {
            return null;
        }
        return new Coordinate(xp, yp, star.getMag(), star.getName());
    }

    // Line 910
    private Coordinate getNorthernCoordinate(Config config, Star star) {
        String[] siderealTimeArray = config.getSiderealTime().split(":");
        double yp = star.getDec();
        double lt = Double.valueOf(config.getLatitudeDegrees());
        double lst = Double.valueOf(siderealTimeArray[0]) + Double.valueOf(siderealTimeArray[1]) / 60.0;
        double xp = star.getRa();

        if ((yp < 0.0)) {
            return null;
        }
        if (this.hasRisen(xp, yp, lt, lst)) {
            return null;
        }
        xp = xp - lst;
        yp = yp * Math.PI / ONE_EIGHTY; // Convert DEC to Radians
        xp = (xp * 15 * Math.PI / ONE_EIGHTY) - HALF_PI; // Convert RA to Radians and Rotate 90 Degrees CounterClockwise
        // double f = 140 / HALF_PI; // Map Scale Factor
        double f = Y_FACTOR / HALF_PI; // Map Scale Factor
        double r1 = f * (HALF_PI - Math.abs(yp));
        // double x = r1 * Math.cos(xp) + 140.0;
        double x = r1 * Math.cos(xp) + Y_FACTOR;
        // double y = r1 * Math.sin(xp) + 160.0 - (1.78 * lt);
        double y = r1 * Math.sin(xp) + X_FACTOR - (VIEW_HORIZON_HEIGHT_RATIO * lt);
        xp = x;
        yp = y;
        if (xp > screen.getWidth() || xp < 0.0 || yp > screen.getHeight() || yp < 0.0) {
        //if (xp > X_FACTOR || xp < 0.0 || yp > Y_FACTOR || yp < 0.0) {
            return null;
        }
        return new Coordinate(xp, yp, star.getMag(), star.getName());
    }

    public double fromHMStoDegrees(double hours, double minute, double second) {
        return hours + ((NEGATIVE_ONE * minute) / SIXTY) + ((NEGATIVE_ONE * second) / THIRTY_SIX_HUNDRED);
    }

    private boolean hasRisen(double xp, double yp, double lt, double lst) {
        boolean hasRisen = false;
        double l = lt * Math.PI / ONE_EIGHTY;
        double d = yp * Math.PI / ONE_EIGHTY;
        double h = (Math.PI * (lst - xp)) / TWELVE_HOURS;
        // Has Risen?
        if (Math.cos(l) * Math.cos(d) * Math.cos(h) < (NEGATIVE_ONE * Math.sin(l)) * Math.sin(d)) {
            hasRisen = true;
        }
        return hasRisen;
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Access methods
    // -----------------------------------------------------------------------------------------------------------------


    public double getSouthernDefaultFactor() {
        return southernDefaultFactor;
    }

    public void setSouthernDefaultFactor(double southernDefaultFactor) {
        this.southernDefaultFactor = southernDefaultFactor;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------------------------------------------------
    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}