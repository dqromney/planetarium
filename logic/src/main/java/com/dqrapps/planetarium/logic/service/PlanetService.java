package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Planet;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates positions of planets using simplified VSOP87 formulas.
 * Provides approximate positions accurate to about 1 degree.
 */
public class PlanetService {

    private static PlanetService instance;

    private PlanetService() {}

    public static synchronized PlanetService getInstance() {
        if (instance == null) {
            instance = new PlanetService();
        }
        return instance;
    }

    /**
     * Calculate positions of all visible planets for given date/time.
     * Returns all 8 planets: Mercury, Venus, Mars, Jupiter, Saturn, Uranus, Neptune.
     * Enhanced with better orbital algorithms and proper coordinates.
     */
    public List<Planet> calculatePlanetPositions(LocalDateTime dateTime) {
        List<Planet> planets = new ArrayList<>();

        // Calculate Julian Date
        double jd = calculateJulianDate(dateTime);
        double T = (jd - 2451545.0) / 36525.0; // Julian centuries from J2000.0

        // Calculate each planet with enhanced algorithms
        planets.add(calculateMercury(T, jd));
        planets.add(calculateVenus(T, jd));
        planets.add(calculateMars(T, jd));
        planets.add(calculateJupiter(T, jd));
        planets.add(calculateSaturn(T, jd));
        planets.add(calculateUranus(T, jd));
        planets.add(calculateNeptune(T, jd));

        return planets;
    }

    /**
     * Calculate Julian Date from LocalDateTime
     */
    private double calculateJulianDate(LocalDateTime dateTime) {
        long epochDay = dateTime.toLocalDate().toEpochDay();
        double dayFraction = dateTime.toLocalTime().toSecondOfDay() / 86400.0;
        return epochDay + 2440587.5 + dayFraction;
    }

    /**
     * Calculate Mercury position using enhanced orbital elements
     */
    private Planet calculateMercury(double T, double jd) {
        // Enhanced orbital elements for Mercury
        double L = normalizeAngle(252.250906 + 149472.674635 * T);  // Mean longitude
        double a = 0.387098;  // Semi-major axis (AU)
        double e = 0.205635 + 0.000020 * T;  // Eccentricity
        double i = 7.004986;  // Inclination (degrees)
        double Omega = 48.330893 - 0.125234 * T;  // Longitude of ascending node
        double w = 77.456119 + 0.158902 * T;  // Longitude of perihelion

        // Calculate position
        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Mercury")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateMercuryMagnitude(pos.distance, pos.phase))
                .distance(pos.distance)
                .phase(pos.phaseDescription)
                .build();
    }

    /**
     * Calculate Venus position using enhanced orbital elements
     */
    private Planet calculateVenus(double T, double jd) {
        double L = normalizeAngle(181.979801 + 58517.815676 * T);
        double a = 0.723330;
        double e = 0.006773 - 0.000042 * T;
        double i = 3.394662;
        double Omega = 76.679920 - 0.278008 * T;
        double w = 131.563707 + 0.048646 * T;

        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Venus")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateVenusMagnitude(pos.distance, pos.phase))
                .distance(pos.distance)
                .phase(pos.phaseDescription)
                .build();
    }

    /**
     * Calculate Mars position using enhanced orbital elements
     */
    private Planet calculateMars(double T, double jd) {
        double L = normalizeAngle(355.433275 + 19140.299331 * T);
        double a = 1.523688;
        double e = 0.093405 + 0.000092 * T;
        double i = 1.849726;
        double Omega = 49.558093 - 0.295025 * T;
        double w = 336.060234 + 0.439292 * T;

        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Mars")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateMarsMagnitude(pos.distance, pos.phase))
                .distance(pos.distance)
                .phase(pos.phaseDescription)
                .build();
    }

    /**
     * Calculate Jupiter position using enhanced orbital elements
     */
    private Planet calculateJupiter(double T, double jd) {
        double L = normalizeAngle(34.351484 + 3034.905700 * T);
        double a = 5.202887;
        double e = 0.048498 - 0.000016 * T;
        double i = 1.303270;
        double Omega = 100.464441 - 0.176174 * T;
        double w = 14.331309 + 0.215581 * T;

        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Jupiter")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateJupiterMagnitude(pos.distance, pos.phase))
                .distance(pos.distance)
                .phase(pos.phaseDescription)
                .build();
    }

    /**
     * Calculate Saturn position using enhanced orbital elements
     */
    private Planet calculateSaturn(double T, double jd) {
        double L = normalizeAngle(50.077471 + 1222.113850 * T);
        double a = 9.536676;
        double e = 0.055546 - 0.000034 * T;
        double i = 2.488878;
        double Omega = 113.665524 - 0.256745 * T;
        double w = 93.056787 + 0.565314 * T;

        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Saturn")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateSaturnMagnitude(pos.distance, pos.phase))
                .distance(pos.distance)
                .phase(pos.phaseDescription)
                .build();
    }

    /**
     * Calculate Uranus position using enhanced orbital elements
     */
    private Planet calculateUranus(double T, double jd) {
        double L = normalizeAngle(314.055005 + 428.466998 * T);
        double a = 19.189916;
        double e = 0.047318 + 0.000007 * T;
        double i = 0.773196;
        double Omega = 74.005947 + 0.521127 * T;
        double w = 173.005159 + 1.486379 * T;

        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Uranus")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateUranusMagnitude(pos.distance))
                .distance(pos.distance)
                .phase("Full") // Outer planets always appear nearly full
                .build();
    }

    /**
     * Calculate Neptune position using enhanced orbital elements
     */
    private Planet calculateNeptune(double T, double jd) {
        double L = normalizeAngle(304.348665 + 218.485952 * T);
        double a = 30.069923;
        double e = 0.008606 + 0.000002 * T;
        double i = 1.770042;
        double Omega = 131.784057 - 0.006173 * T;
        double w = 48.123691 + 1.426294 * T;

        PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

        return Planet.builder()
                .name("Neptune")
                .ra(pos.ra)
                .dec(pos.dec)
                .magnitude(calculateNeptuneMagnitude(pos.distance))
                .distance(pos.distance)
                .phase("Full") // Outer planets always appear nearly full
                .build();
    }
}

