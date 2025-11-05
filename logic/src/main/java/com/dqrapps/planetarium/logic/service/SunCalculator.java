package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.SunPosition;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Calculates the position of the Sun using simplified astronomical algorithms.
 * Based on Jean Meeus's "Astronomical Algorithms" with simplifications for educational use.
 */
public class SunCalculator {

    // J2000.0 epoch (January 1, 2000, 12:00 UTC)
    private static final double J2000 = 2451545.0;

    /**
     * Calculate the Sun's position for a given date, time, and observer location.
     *
     * @param dateTime The date and time (local time)
     * @param latitude Observer's latitude in degrees
     * @param longitude Observer's longitude in degrees (positive East)
     * @return SunPosition object with RA, Dec, altitude, azimuth
     */
    public static SunPosition calculateSunPosition(LocalDateTime dateTime, double latitude, double longitude) {
        // Convert to Julian Date
        double jd = toJulianDate(dateTime);

        // Days since J2000.0
        double n = jd - J2000;

        // Mean longitude of the Sun (degrees)
        double L = (280.460 + 0.9856474 * n) % 360.0;
        if (L < 0) L += 360.0;

        // Mean anomaly of the Sun (degrees)
        double g = Math.toRadians((357.528 + 0.9856003 * n) % 360.0);

        // Ecliptic longitude of the Sun (degrees)
        double lambda = L + 1.915 * Math.sin(g) + 0.020 * Math.sin(2 * g);
        lambda = Math.toRadians(lambda);

        // Obliquity of the ecliptic (degrees)
        double epsilon = Math.toRadians(23.439 - 0.0000004 * n);

        // Convert ecliptic to equatorial coordinates
        double alpha = Math.atan2(Math.cos(epsilon) * Math.sin(lambda), Math.cos(lambda));
        double delta = Math.asin(Math.sin(epsilon) * Math.sin(lambda));

        // Convert to hours and degrees
        double ra = Math.toDegrees(alpha) / 15.0;  // RA in hours
        if (ra < 0) ra += 24.0;
        double dec = Math.toDegrees(delta);        // Dec in degrees

        // Calculate Local Sidereal Time
        double lst = calculateLocalSiderealTime(dateTime, longitude);

        // Calculate hour angle
        double hourAngle = lst - ra;
        if (hourAngle < 0) hourAngle += 24.0;
        if (hourAngle > 24.0) hourAngle -= 24.0;

        // Convert hour angle to radians for calculations
        double h = Math.toRadians(hourAngle * 15.0);  // Convert hours to degrees, then to radians
        double lat = Math.toRadians(latitude);
        double decRad = Math.toRadians(dec);

        // Calculate altitude and azimuth
        double altitude = Math.asin(
            Math.sin(lat) * Math.sin(decRad) +
            Math.cos(lat) * Math.cos(decRad) * Math.cos(h)
        );

        double azimuth = Math.atan2(
            -Math.sin(h),
            Math.tan(decRad) * Math.cos(lat) - Math.sin(lat) * Math.cos(h)
        );

        // Convert to degrees
        altitude = Math.toDegrees(altitude);
        azimuth = Math.toDegrees(azimuth);
        if (azimuth < 0) azimuth += 360.0;

        return new SunPosition(ra, dec, altitude, azimuth);
    }

    /**
     * Convert LocalDateTime to Julian Date.
     */
    private static double toJulianDate(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        double hour = dateTime.getHour() + dateTime.getMinute() / 60.0 + dateTime.getSecond() / 3600.0;

        // Adjust for January and February
        if (month <= 2) {
            year -= 1;
            month += 12;
        }

        // Calculate Julian Date
        int a = year / 100;
        int b = 2 - a + (a / 4);

        double jd = Math.floor(365.25 * (year + 4716)) +
                   Math.floor(30.6001 * (month + 1)) +
                   day + hour / 24.0 + b - 1524.5;

        return jd;
    }

    /**
     * Calculate Local Sidereal Time in hours.
     */
    private static double calculateLocalSiderealTime(LocalDateTime dateTime, double longitude) {
        double jd = toJulianDate(dateTime);

        // Days since J2000.0
        double n = jd - J2000;

        // Greenwich Mean Sidereal Time at 0h UT
        double gmst = (18.697374558 + 24.06570982441908 * n) % 24.0;
        if (gmst < 0) gmst += 24.0;

        // Convert longitude to hours and add to get Local Sidereal Time
        double lst = gmst + longitude / 15.0;
        if (lst < 0) lst += 24.0;
        if (lst >= 24.0) lst -= 24.0;

        return lst;
    }

    /**
     * Calculate sunrise time for a given date and location.
     * Returns null if sun doesn't rise (polar night) or set (polar day).
     */
    public static LocalDateTime calculateSunrise(LocalDateTime date, double latitude, double longitude) {
        return calculateSunEvent(date, latitude, longitude, true);
    }

    /**
     * Calculate sunset time for a given date and location.
     * Returns null if sun doesn't rise (polar night) or set (polar day).
     */
    public static LocalDateTime calculateSunset(LocalDateTime date, double latitude, double longitude) {
        return calculateSunEvent(date, latitude, longitude, false);
    }

    /**
     * Calculate sunrise or sunset time using iterative method.
     */
    private static LocalDateTime calculateSunEvent(LocalDateTime date, double latitude, double longitude, boolean sunrise) {
        // Start with noon for the given date
        LocalDateTime guess = date.withHour(12).withMinute(0).withSecond(0);

        // Iterate to find when sun crosses horizon
        for (int i = 0; i < 10; i++) {  // Max 10 iterations
            SunPosition pos = calculateSunPosition(guess, latitude, longitude);

            if (Math.abs(pos.getAltitude()) < 0.1) {  // Close enough to horizon
                return guess;
            }

            // Adjust guess based on current altitude
            // Sun moves ~15 degrees per hour
            double hoursToHorizon = -pos.getAltitude() / 15.0;

            if (sunrise) {
                hoursToHorizon = -hoursToHorizon;
            }

            guess = guess.plus((long)(hoursToHorizon * 60), ChronoUnit.MINUTES);
        }

        return null;  // Couldn't find sunrise/sunset (polar regions)
    }
}
