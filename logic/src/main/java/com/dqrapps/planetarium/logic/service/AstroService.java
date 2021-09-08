package com.dqrapps.planetarium.logic.service;

//import static com.mhuss.AstroLib.Astro.DAYS_PER_HOUR;

import com.dqrapps.planetarium.logic.model.Config;
import com.dqrapps.planetarium.logic.model.Coordinate;
import com.dqrapps.planetarium.logic.model.Hemisphere;
import com.dqrapps.planetarium.logic.model.Star;
import com.mhuss.AstroLib.TimeOps;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Locale;

public class AstroService {
    private static double THREE_SIXTY = 360.0;
    private static double ONE_EIGHTY = THREE_SIXTY / 2.0;
    private static double NINTY = ONE_EIGHTY / 2.0;
    private static double FORTY_FIVE = NINTY / 2.0;
    private static double TWELVE_HOURS = 12.0;
    private static double NEGATIVE_ONE = -1.0;
    private static double DAYS_PER_HOUR = 1.0 / 24.0;
    private static double SIXTY = 60.0;
    private static double THIRTY_SIX_HUNDRED = 3600.0;

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

    /**
     *
     */
    // TODO Not sure this is working yet
    // public boolean isVisible(Hemisphere hemisphere, double decDegsYP, double latDegsLT) {
    public boolean isVisible(Hemisphere hemisphere, Config config, Star star) {
        String[] siderealTimeArray = config.getSiderealTime().split(":");
        boolean isVisible = false;
        double yp = star.getDec();
        double lt = Double.valueOf(config.getLatitudeDegrees());
        double lst = Double.valueOf(siderealTimeArray[0]) + Double.valueOf(siderealTimeArray[1]) / 60.0;
        double xp = star.getRa();
        double l,d,h;
        boolean hasRisen;
        int hourOfDay = LocalDateTime.now().getHour();
        boolean isNorthern = hemisphere.equals(Hemisphere.NORTH);

//        if ((yp > lt)) {
//            System.out.println("Check 1 - Northern star");
//            isVisible = isNorthern;
//        } else {
//            System.out.println("Check 1 - Southern star");
//            isVisible = !isNorthern;
//        }
//        if ((yp < (lt - 90))) {
//            System.out.println("Check 2 - Northern star");
//            isVisible = isNorthern;
//        } else {
//            System.out.println("Check 2 - Southern star");
//            isVisible = !isNorthern;
//        }
        l = lt * Math.PI / ONE_EIGHTY;
        d = yp * Math.PI / ONE_EIGHTY;
        h = (Math.PI * (hourOfDay - xp)) / TWELVE_HOURS;
        hasRisen = Math.cos(l) * Math.cos(d) * Math.cos(h) < (NEGATIVE_ONE * Math.sin(l)) * Math.sin(d);
        if (hasRisen) {
            System.out.println("Check 3 - Northern star");
            isVisible = isNorthern;
        } else {
            System.out.println("Check 3 - Southern star");
            isVisible = !isNorthern;
        }
        return isVisible;
    }

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
        double l,d,h;
        int hourOfDay = LocalDateTime.now().getHour();

        if ((yp > lt)) {
            return null;
        }
        if ((yp < (lt - 90))) {
            return null;
        }
        l = lt * Math.PI / ONE_EIGHTY;
        d = yp * Math.PI / ONE_EIGHTY;
        h = (Math.PI * (lst - xp)) / TWELVE_HOURS;
        // Has Risen?.
        if (Math.cos(l) * Math.cos(d) * Math.cos(h) < (NEGATIVE_ONE * Math.sin(l)) * Math.sin(d)) {
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
        // Allows 12 Hours of Right Ascention
        xp = 140 + xp * 23.33;
        if (xp > 279 || xp < 0.0) {
            return null;
        }
        // Allow 90 Degree of Declination
        yp = 1.78 * (lt - yp);
        if (yp < 0 || yp > 159) {
            return null;
        }
        return new Coordinate(xp, yp);
    }

    // Line 910
    private Coordinate getNorthernCoordinate(Config config, Star star) {
        String[] siderealTimeArray = config.getSiderealTime().split(":");
        double yp = star.getDec();
        double lt = Double.valueOf(config.getLatitudeDegrees());
        double lst = Double.valueOf(siderealTimeArray[0]) + Double.valueOf(siderealTimeArray[1]) / 60.0;
        double xp = star.getRa();
        double l,d,h;
        int hourOfDay = LocalDateTime.now().getHour();

        if ((yp < 0.0)) {
            return null;
        }
        l = lt * Math.PI / ONE_EIGHTY;
        d = yp * Math.PI / ONE_EIGHTY;
        h = (Math.PI * (lst - xp)) / TWELVE_HOURS;
        // Has Risen?.
        if (Math.cos(l) * Math.cos(d) * Math.cos(h) < (NEGATIVE_ONE * Math.sin(l)) * Math.sin(d)) {
            return null;
        }
        xp = xp - lst;
        yp = yp * Math.PI / ONE_EIGHTY; // Convert DEC to Radians
        xp = (xp * 15 * Math.PI / 180) - Math.PI/2.0; // Convert RA to Radians and Rotate 90 Degrees CounterClockwise
        double f = 140 / (Math.PI / 2.0); // Map Scale Factor
        double r1 = f * ((Math.PI/2.0) - Math.abs(yp));
        double x = r1 * Math.cos(xp) + 140.0;
        double y = r1 * Math.sin(xp) + 160.0 - (1.78 * lt);
        xp = x;
        yp = y;
        if (xp > 279.0 || xp < 0.0 || yp > 159.0 || yp < 0.0) {
            return null;
        }
        return new Coordinate(xp, yp);
    }

    /**
     *
     * @param decDegsYP
     * @param latDegsLT
     * @return
     */
        public Hemisphere getHemisphere(double decDegsYP, double latDegsLT) {
//    public Hemisphere getHemisphere(double latDegsLT, double decDegsYP) {
        Hemisphere cp = Hemisphere.SOUTH;
        boolean isNorth = (decDegsYP > latDegsLT) || (decDegsYP < (latDegsLT - 90.0));
        if (isNorth) {
            cp = Hemisphere.NORTH;
        }
        return cp;
    }

    /**
     * From Hours, Minutes, and Seconds to Degrees.
     * @param hours
     * @param minute
     * @param second
     * @return
     */
    public double fromHMStoDegrees(double hours, double minute, double second) {
        return hours + ((NEGATIVE_ONE * minute) / SIXTY) + ((NEGATIVE_ONE * second) / THIRTY_SIX_HUNDRED);
    }

}