package com.dqrapps.planetarium.logic.service;

import static com.mhuss.AstroLib.Astro.DAYS_PER_HOUR;

public class AstroService {
    private static double THREE_SIXTY = 360.0;
    private static double ONE_EIGHTY = THREE_SIXTY / 2.0;
    private static double NINTY = ONE_EIGHTY / 2.0;
    private static double FORTY_FIVE = NINTY / 2.0;
    private static double TWELVE_HOURS = 12.0;
    private static double NEGATIVE_ONE = -1.0;

/*
780  REM  CIRCUMPOLARS IN N. SKY
790  IF YP > LT THEN RC = 1: RETURN
800  IF YP < LT - 90 THEN RC = 1: RETURN
810  REM  SOUTHERN CUTOFF
820  LET L = LT * 3.14 / 180: LET D = YP * 3.14 / 180

Legend:
XP - Right Ascension or RA
YP - Declination or DEC
RC - Northern (1) or Southern (0) horizon
LST - Current time expressed as HR + (MIN / 60) + (SEC / 1800)
HZ$ - Input horizon, i.e. 'N' or 'S'.
W = 265
LNH - Longitude Hours, i.e. 71 (-180 to 180)
LGM - Longitude Minutes (degrees), i.e. 3 min
LT = Latitude, i.e. 42 degree (0-90)
HR = Sidereal Hour
MIN = Sidereal Minute
*/

    /**
     *
     */

    public boolean isVisible(double decDegs, double latDegs) {
        double latInDegs = toDegrees(latDegs);
        double decInDegs = toDegrees(decDegs);
        double hourAngleRadians = Math.PI * (latInDegs - decInDegs) / TWELVE_HOURS;
        double dph = DAYS_PER_HOUR;
        return Math.cos(latInDegs) * Math.cos(decInDegs) * Math.cos(hourAngleRadians) >=
                (NEGATIVE_ONE * Math.sin(latInDegs)) * Math.sin(decInDegs);
    }

    public double toDegrees(double value) {
        return value * Math.PI / ONE_EIGHTY;
    }
}

