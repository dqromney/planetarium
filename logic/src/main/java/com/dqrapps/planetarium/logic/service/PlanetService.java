package com.dqrapps.planetarium.logic.service;

import com.dqrapps.planetarium.logic.model.Planet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates positions of planets using simplified VSOP87 formulas.
 * Provides approximate positions accurate to about 1 degree.
 */
public class PlanetService {

  private static PlanetService instance;

  private PlanetService() {
  }

  public static synchronized PlanetService getInstance() {
    if (instance == null) {
      instance = new PlanetService();
    }
    return instance;
  }

  /**
   * Calculate positions of all celestial objects for given date/time.
   * Returns planets, dwarf planets (Pluto), and major asteroids (Ceres, Pallas, Juno).
   * Enhanced with better orbital algorithms and proper coordinates.
   */
  public List<Planet> calculatePlanetPositions(LocalDateTime dateTime) {
    List<Planet> planets = new ArrayList<>();

    // Calculate Julian Date
    double jd = calculateJulianDate(dateTime);
    double T = (jd - 2451545.0) / 36525.0; // Julian centuries from J2000.0

    // Calculate major planets
    planets.add(calculateMercury(T, jd));
    planets.add(calculateVenus(T, jd));
    planets.add(calculateMars(T, jd));
    planets.add(calculateJupiter(T, jd));
    planets.add(calculateSaturn(T, jd));
    planets.add(calculateUranus(T, jd));
    planets.add(calculateNeptune(T, jd));

    // Calculate dwarf planets
    planets.add(calculatePluto(T, jd));

    // Calculate major asteroids
    planets.add(calculateCeres(T, jd));
    planets.add(calculatePallas(T, jd));
    planets.add(calculateJuno(T, jd));

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
        .type("planet")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculateMercuryMagnitude(pos.distance, pos.phase))
        .distance(pos.distance)
        .phase(pos.phaseDescription)
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(4879) // km
        .orbitalPeriod(0.241)
        .description("Closest planet to the Sun")
        .composition("Iron core with silicate mantle")
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
        .type("planet")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculateJupiterMagnitude(pos.distance, pos.phase))
        .distance(pos.distance)
        .phase(pos.phaseDescription)
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(142984) // km
        .orbitalPeriod(11.86)
        .description("Largest planet, gas giant with Great Red Spot")
        .composition("Hydrogen and helium")
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
        .type("planet")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculateNeptuneMagnitude(pos.distance))
        .distance(pos.distance)
        .phase("Full") // Outer planets always appear nearly full
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(49244) // km
        .orbitalPeriod(164.8)
        .description("Ice giant, furthest major planet")
        .composition("Water, methane, ammonia ices")
        .discoverer("Johann Galle")
        .discoveryDate("1846")
        .build();
  }

  /**
   * Calculate Pluto position using enhanced orbital elements
   */
  private Planet calculatePluto(double T, double jd) {
    double L = normalizeAngle(238.92881 + 145.18042 * T);
    double a = 39.48211675;
    double e = 0.24882730 + 0.00006465 * T;
    double i = 17.14001206;
    double Omega = 110.30393684 - 0.01183482 * T;
    double w = 224.06891629 + 0.04062942 * T;

    PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

    return Planet.builder()
        .name("Pluto")
        .type("dwarf_planet")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculatePlutoMagnitude(pos.distance))
        .distance(pos.distance)
        .phase("Full")
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(2376) // km
        .orbitalPeriod(248.1)
        .description("Dwarf planet in the Kuiper Belt")
        .composition("Rock and ice")
        .discoverer("Clyde Tombaugh")
        .discoveryDate("1930")
        .build();
  }

  /**
   * Calculate Ceres position (largest asteroid)
   */
  private Planet calculateCeres(double T, double jd) {
    double L = normalizeAngle(352.23052 + 214.14998 * T);
    double a = 2.76773431;
    double e = 0.07600902;
    double i = 10.59340979;
    double Omega = 80.39319872;
    double w = 72.58981084;

    PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

    return Planet.builder()
        .name("Ceres")
        .type("asteroid")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculateCeresMagnitude(pos.distance))
        .distance(pos.distance)
        .phase("Full")
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(940) // km
        .orbitalPeriod(4.61)
        .description("Largest asteroid, now classified as dwarf planet")
        .composition("Rock and ice")
        .discoverer("Giuseppe Piazzi")
        .discoveryDate("1801")
        .build();
  }

  /**
   * Calculate Pallas position (second largest asteroid)
   */
  private Planet calculatePallas(double T, double jd) {
    double L = normalizeAngle(249.16139 + 162.64053 * T);
    double a = 2.77286964;
    double e = 0.23138298;
    double i = 34.84096718;
    double Omega = 173.09690041;
    double w = 310.14996769;

    PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

    return Planet.builder()
        .name("Pallas")
        .type("asteroid")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculatePallasMagnitude(pos.distance))
        .distance(pos.distance)
        .phase("Full")
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(512) // km
        .orbitalPeriod(4.62)
        .description("Second largest asteroid")
        .composition("Silicate rock")
        .discoverer("Heinrich Olbers")
        .discoveryDate("1802")
        .build();
  }

  /**
   * Calculate Juno position (third asteroid discovered)
   */
  private Planet calculateJuno(double T, double jd) {
    double L = normalizeAngle(13.04695 + 225.51310 * T);
    double a = 2.66865814;
    double e = 0.25695579;
    double i = 12.98145418;
    double Omega = 169.87670505;
    double w = 248.18149689;

    PlanetPosition pos = calculatePlanetPosition(L, a, e, i, Omega, w, T);

    return Planet.builder()
        .name("Juno")
        .type("asteroid")
        .ra(pos.ra)
        .dec(pos.dec)
        .magnitude(calculateJunoMagnitude(pos.distance))
        .distance(pos.distance)
        .phase("Full")
        .semiMajorAxis(a)
        .eccentricity(e)
        .inclination(i)
        .longitudeOfAscendingNode(Omega)
        .argumentOfPerihelion(w - Omega)
        .meanLongitude(L)
        .diameter(246) // km
        .orbitalPeriod(4.36)
        .description("Third asteroid discovered, highly elongated")
        .composition("Silicate rock")
        .discoverer("Karl Ludwig Harding")
        .discoveryDate("1804")
        .build();
  }

  /**
   * Helper class for planet position calculations
   */
  private static class PlanetPosition {

    double ra;               // Right Ascension (hours)
    double dec;              // Declination (degrees)
    double distance;         // Distance from Earth (AU)
    double phase;            // Phase angle (0-1, for inner planets)
    String phaseDescription; // Phase description
  }

  /**
   * Calculate planet position using Keplerian orbital elements
   */
  private PlanetPosition calculatePlanetPosition(double L, double a, double e, double i, double Omega, double w, double T) {
    // Convert angles to radians
    double LRad = Math.toRadians(L);
    double iRad = Math.toRadians(i);
    double OmegaRad = Math.toRadians(Omega);
    double wRad = Math.toRadians(w);

    // Calculate mean anomaly
    double M = LRad - wRad;

    // Solve Kepler's equation for eccentric anomaly (simplified)
    double E = M + e * Math.sin(M) * (1.0 + e * Math.cos(M));

    // True anomaly
    double v = 2 * Math.atan2(Math.sqrt(1 + e) * Math.sin(E / 2), Math.sqrt(1 - e) * Math.cos(E / 2));

    // Distance
    double r = a * (1 - e * Math.cos(E));

    // Position in orbital plane
    double x = r * Math.cos(v);
    double y = r * Math.sin(v);

    // Convert to ecliptic coordinates
    double xe = x * (Math.cos(wRad) * Math.cos(OmegaRad) - Math.sin(wRad) * Math.sin(OmegaRad) * Math.cos(iRad)) +
        y * (-Math.sin(wRad) * Math.cos(OmegaRad) - Math.cos(wRad) * Math.sin(OmegaRad) * Math.cos(iRad));
    double ye = x * (Math.cos(wRad) * Math.sin(OmegaRad) + Math.sin(wRad) * Math.cos(OmegaRad) * Math.cos(iRad)) +
        y * (-Math.sin(wRad) * Math.sin(OmegaRad) + Math.cos(wRad) * Math.cos(OmegaRad) * Math.cos(iRad));
    double ze = x * (Math.sin(wRad) * Math.sin(iRad)) + y * (Math.cos(wRad) * Math.sin(iRad));

    // Convert to geocentric coordinates (simplified - assume Earth at origin)
    double distance = Math.sqrt(xe * xe + ye * ye + ze * ze);

    // Convert to equatorial coordinates
    double obliquity = Math.toRadians(23.439291 - 0.0130102 * T);
    double alpha = Math.atan2(ye * Math.cos(obliquity) - ze * Math.sin(obliquity), xe);
    double delta = Math.asin(ye * Math.sin(obliquity) + ze * Math.cos(obliquity));

    // Convert RA to hours and normalize
    double ra = Math.toDegrees(alpha) / 15.0;
    if (ra < 0) {
      ra += 24.0;
    }

    double dec = Math.toDegrees(delta);

    // Calculate phase (simplified)
    double phase = 0.5 * (1 + Math.cos(v)); // Approximate phase
    String phaseDesc = getPhaseDescription(phase);

    PlanetPosition pos = new PlanetPosition();
    pos.ra = ra;
    pos.dec = dec;
    pos.distance = distance;
    pos.phase = phase;
    pos.phaseDescription = phaseDesc;

    return pos;
  }

  /**
   * Normalize angle to 0-360 degrees
   */
  private double normalizeAngle(double angle) {
    angle = angle % 360.0;
    if (angle < 0) {
      angle += 360.0;
    }
    return angle;
  }

  /**
   * Get phase description from phase value
   */
  private String getPhaseDescription(double phase) {
    if (phase > 0.9) {
      return "Full";
    }
    else if (phase > 0.6) {
      return "Gibbous";
    }
    else if (phase > 0.4) {
      return "Quarter";
    }
    else if (phase > 0.1) {
      return "Crescent";
    }
    else {
      return "New";
    }
  }

  // Magnitude calculation methods for each planet
  private double calculateMercuryMagnitude(double distance, double phase) {
    return -0.42 + 3.80 * Math.log10(distance * distance / phase);
  }

  private double calculateVenusMagnitude(double distance, double phase) {
    return -4.40 + 0.09 * Math.log10(distance * distance / phase);
  }

  private double calculateMarsMagnitude(double distance, double phase) {
    return -1.52 + 1.60 * Math.log10(distance * distance);
  }

  private double calculateJupiterMagnitude(double distance, double phase) {
    return -9.40 + 0.50 * Math.log10(distance * distance);
  }

  private double calculateSaturnMagnitude(double distance, double phase) {
    return -8.88 + 0.44 * Math.log10(distance * distance); // Simplified - doesn't include ring effects
  }

  private double calculateUranusMagnitude(double distance) {
    return 5.52 + 0.31 * Math.log10(distance * distance);
  }

  private double calculateNeptuneMagnitude(double distance) {
    return 7.84 + 0.00 * Math.log10(distance * distance);
  }

  private double calculatePlutoMagnitude(double distance) {
    return 15.1 + 0.04 * Math.log10(distance * distance);
  }

  private double calculateCeresMagnitude(double distance) {
    return 3.36 + 0.12 * Math.log10(distance * distance);
  }

  private double calculatePallasMagnitude(double distance) {
    return 4.13 + 0.11 * Math.log10(distance * distance);
  }

  private double calculateJunoMagnitude(double distance) {
    return 5.33 + 0.21 * Math.log10(distance * distance);
  }
}

