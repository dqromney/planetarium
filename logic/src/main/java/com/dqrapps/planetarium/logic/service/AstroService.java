package com.dqrapps.planetarium.logic.service;

//import static com.mhuss.AstroLib.Astro.DAYS_PER_HOUR;

import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Coordinate;
import com.dqrapps.planetarium.logic.model.Hemisphere;
import com.dqrapps.planetarium.logic.model.Star;

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
    public static double X_FACTOR = 160; //(800.0/SCREEN_RATIO); // 160.0
    public static double Y_FACTOR = 140; //(600.0/SCREEN_RATIO); // 140.0
    // 600/X = 1.28 -> 600/1.28 = X

/*
780  REM  CIRCUMPOLARS IN N. SKY
790  IF YP > LT THEN RC = 1: RETURN
800  IF YP < LT - 90 THEN RC = 1: RETURN
810  REM  SOUTHERN CUTOFF
820  LET L = LT * 3.14 / 180: LET D = YP * 3.14 / 180

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

    // Apple II Plus - Plot hires point/line (x=0...279, y=0...191)
    public Coordinate getCoordinate(Hemisphere hemisphere, Config config, Star star) {
        Coordinate coordinate;
        if (Hemisphere.NORTH.equals(hemisphere)) {
            coordinate = getNorthernCoordinate(config, star);
        } else {
            coordinate = getSouthernCoordinate(config, star);
        }
        return coordinate;
    }

    // Line 780
    private Coordinate getSouthernCoordinate(Config config, Star star) {
        String[] siderealTimeArray = config.getSiderealTime().split(":");
        double yp = star.getDec();
        double lt = Double.valueOf(config.getLatitudeDegrees());
        double lst = Double.valueOf(siderealTimeArray[0]) + Double.valueOf(siderealTimeArray[1]) / 60.0;
        double xp = star.getRa();

        if ((yp > lt)) {
            return null;
        }
        if ((yp < (lt - 90))) {
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
        xp = 140 + xp * 23.33;
        //xp = Y_FACTOR + xp * 23.33;
        if (xp > 279 || xp < 0.0) {
        //if (xp > X_FACTOR || xp < 0.0) {
            return null;
        }
        // Allow 90 Degree of Declination
        yp = 1.78 * (lt - yp);
        // if (yp < 0 || yp > 159) {
        if (yp < 0 || yp > X_FACTOR-1) {
            return null;
        }
        return new Coordinate(xp, yp, star.getMag());
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
        double y = r1 * Math.sin(xp) + X_FACTOR - (1.78 * lt);
        xp = x;
        yp = y;
        if (xp > 279.0 || xp < 0.0 || yp > 159.0 || yp < 0.0) {
        //if (xp > X_FACTOR || xp < 0.0 || yp > Y_FACTOR || yp < 0.0) {
            return null;
        }
        return new Coordinate(xp, yp, star.getMag());
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
}