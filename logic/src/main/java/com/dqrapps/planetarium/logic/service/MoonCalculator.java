package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.MoonPosition;
import java.time.LocalDateTime;

/**
 * Calculates the position and phase of the Moon using simplified astronomical algorithms.
 * Based on Jean Meeus's "Astronomical Algorithms" with simplifications for educational use.
 */
public class MoonCalculator {

    // J2000.0 epoch (January 1, 2000, 12:00 UTC)
    private static final double J2000 = 2451545.0;

    /**
     * Calculate the Moon's position and phase for a given date, time, and observer location.
     *
     * @param dateTime The date and time (local time)
     * @param latitude Observer's latitude in degrees
     * @param longitude Observer's longitude in degrees (positive East)
     * @return MoonPosition object with RA, Dec, altitude, azimuth, and phase information
     */
    public static MoonPosition calculateMoonPosition(LocalDateTime dateTime, double latitude, double longitude) {
        // Convert to Julian Date
        double jd = toJulianDate(dateTime);

        // Days since J2000.0
        double d = jd - J2000;

        // Moon's mean orbital elements (simplified)
        double L = Math.toRadians((218.316 + 13.176396 * d) % 360.0);  // Mean longitude
        double M = Math.toRadians((134.963 + 13.064993 * d) % 360.0);  // Mean anomaly
        double F = Math.toRadians((93.272 + 13.229350 * d) % 360.0);   // Argument of latitude

        // Calculate ecliptic longitude with perturbations (simplified)
        double longitude_moon = L + Math.toRadians(
            6.289 * Math.sin(M) +
            1.274 * Math.sin(2 * L - M) +
            0.658 * Math.sin(2 * L) +
            0.214 * Math.sin(2 * M) +
            0.110 * Math.sin(M + 2 * L)
        );

        // Calculate ecliptic latitude with perturbations (simplified)
        double latitude_moon = Math.toRadians(
            5.128 * Math.sin(F) +
            0.281 * Math.sin(M + F) +
            0.278 * Math.sin(M - F) +
            0.173 * Math.sin(2 * L - F)
        );

        // Calculate distance (not used for position but useful for future enhancements)
        double distance = 385000.56 +
            20905.355 * Math.cos(M) +
            3699.111 * Math.cos(2 * L - M) +
            2955.968 * Math.cos(2 * L) +
            569.925 * Math.cos(2 * M);

        // Obliquity of the ecliptic
        double epsilon = Math.toRadians(23.439 - 0.0000004 * d);

        // Convert ecliptic to equatorial coordinates
        double alpha = Math.atan2(
            Math.sin(longitude_moon) * Math.cos(epsilon) - Math.tan(latitude_moon) * Math.sin(epsilon),
            Math.cos(longitude_moon)
        );

        double delta = Math.asin(
            Math.sin(latitude_moon) * Math.cos(epsilon) +
            Math.cos(latitude_moon) * Math.sin(epsilon) * Math.sin(longitude_moon)
        );

        // Convert to hours and degrees
        double ra = Math.toDegrees(alpha) / 15.0;  // RA in hours
        if (ra < 0) ra += 24.0;
        double dec = Math.toDegrees(delta);        // Dec in degrees

        // Calculate phase
        double sunLongitude = calculateSunEclipticLongitude(d);
        double phaseAngle = longitude_moon - sunLongitude;
        double illumination = (1 + Math.cos(phaseAngle)) / 2;  // 0 = new, 1 = full

        // Determine phase name
        String phaseName = getMoonPhaseName(illumination, phaseAngle);

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

        return new MoonPosition(ra, dec, altitude, azimuth, illumination, phaseName);
    }

    /**
     * Calculate the Sun's ecliptic longitude for phase calculations.
     */
    private static double calculateSunEclipticLongitude(double d) {
        // Mean longitude of the Sun
        double L = Math.toRadians((280.460 + 0.9856474 * d) % 360.0);

        // Mean anomaly of the Sun
        double g = Math.toRadians((357.528 + 0.9856003 * d) % 360.0);

        // Ecliptic longitude with equation of center
        return L + Math.toRadians(1.915 * Math.sin(g) + 0.020 * Math.sin(2 * g));
    }

    /**
     * Determine the moon phase name based on illumination and phase angle.
     */
    private static String getMoonPhaseName(double illumination, double phaseAngle) {
        // Normalize phase angle to 0-2π
        phaseAngle = phaseAngle % (2 * Math.PI);
        if (phaseAngle < 0) phaseAngle += 2 * Math.PI;

        // Convert to degrees for easier comparison
        double phaseDegrees = Math.toDegrees(phaseAngle);

        if (illumination < 0.05) {
            return "New Moon";
        } else if (phaseDegrees < 90 && illumination < 0.5) {
            return "Waxing Crescent";
        } else if (phaseDegrees >= 80 && phaseDegrees <= 100 && illumination >= 0.4 && illumination <= 0.6) {
            return "First Quarter";
        } else if (phaseDegrees < 180 && illumination < 0.95) {
            return "Waxing Gibbous";
        } else if (illumination >= 0.95) {
            return "Full Moon";
        } else if (phaseDegrees > 180 && phaseDegrees < 270 && illumination > 0.5) {
            return "Waning Gibbous";
        } else if (phaseDegrees >= 260 && phaseDegrees <= 280 && illumination >= 0.4 && illumination <= 0.6) {
            return "Last Quarter";
        } else {
            return "Waning Crescent";
        }
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
}
