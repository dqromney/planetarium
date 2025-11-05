package com.dqrapps.planetarium.test;

import com.dqrapps.planetarium.logic.model.SunPosition;
import com.dqrapps.planetarium.logic.model.MoonPosition;
import com.dqrapps.planetarium.logic.service.SunCalculator;
import com.dqrapps.planetarium.logic.service.MoonCalculator;
import java.time.LocalDateTime;

/**
 * Simple test to verify Sun and Moon calculations are working.
 */
public class SunMoonTest {
    public static void main(String[] args) {
        // Test with current time and Salt Lake City coordinates
        LocalDateTime now = LocalDateTime.now();
        double latitude = 40.7608; // Salt Lake City
        double longitude = -111.8910;

        System.out.println("=== Sun and Moon Position Test ===");
        System.out.println("Date/Time: " + now);
        System.out.println("Location: " + latitude + "°N, " + Math.abs(longitude) + "°W");
        System.out.println();

        try {
            // Test Sun calculation
            SunPosition sun = SunCalculator.calculateSunPosition(now, latitude, longitude);
            System.out.println("☀ SUN POSITION:");
            System.out.println("  RA: " + String.format("%.3f", sun.getRa()) + "h");
            System.out.println("  Dec: " + String.format("%.2f", sun.getDec()) + "°");
            System.out.println("  Altitude: " + String.format("%.2f", sun.getAltitude()) + "°");
            System.out.println("  Azimuth: " + String.format("%.2f", sun.getAzimuth()) + "°");
            System.out.println("  Status: " + sun.getDisplayStatus());
            System.out.println();

            // Test Moon calculation
            MoonPosition moon = MoonCalculator.calculateMoonPosition(now, latitude, longitude);
            System.out.println("🌙 MOON POSITION:");
            System.out.println("  RA: " + String.format("%.3f", moon.getRa()) + "h");
            System.out.println("  Dec: " + String.format("%.2f", moon.getDec()) + "°");
            System.out.println("  Altitude: " + String.format("%.2f", moon.getAltitude()) + "°");
            System.out.println("  Azimuth: " + String.format("%.2f", moon.getAzimuth()) + "°");
            System.out.println("  Phase: " + moon.getPhaseName());
            System.out.println("  Illumination: " + moon.getIlluminationPercent() + "%");
            System.out.println("  Emoji: " + moon.getPhaseEmoji());
            System.out.println("  Status: " + moon.getDisplayStatus());
            System.out.println();

            System.out.println("✅ Sun and Moon calculations completed successfully!");

        } catch (Exception e) {
            System.out.println("❌ Error in calculations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
