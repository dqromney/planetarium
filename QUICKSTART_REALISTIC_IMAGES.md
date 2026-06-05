# Quick Start: Realistic Celestial Images

## 🚀 Get Stunning Visuals in 3 Steps

---

## Step 1: Setup the Image System (5 minutes)

### Add the Image Service

1. **Create directory structure**:
   ```bash
   ./download-sample-images.sh
   ```
   This creates the image directory and setup files.

2. **Add import to PlotController.java** (line ~4):
   ```java
   import com.dqrapps.planetarium.gui.image.CelestialImageService;
   import javafx.scene.image.Image;
   ```

3. **Add field to PlotController.java** (line ~140):
   ```java
   // Celestial body image service for realistic rendering
   private CelestialImageService imageService;
   ```

4. **Initialize in initialize() method**:
   ```java
   @FXML
   public void initialize() {
       // ... existing code ...
       
       // Initialize image service
       imageService = CelestialImageService.getInstance();
       
       // ... rest of code ...
   }
   ```

---

## Step 2: Update Rendering Methods (10 minutes)

Replace these three methods in `PlotController.java`:

### Replace drawSun() - Around Line 1495

<details>
<summary>Click to expand code</summary>

```java
private void drawSun() {
    if (!showSun || currentSunPosition == null || projection == null) {
        return;
    }

    if (currentSunPosition.isVisible()) {
        double[] coords = projection.raDecToScreen(currentSunPosition.getRa(), currentSunPosition.getDec());
        if (coords != null) {
            double x = coords[0];
            double y = coords[1];
            double size = imageService.calculateAutoSize("sun", 1.0, 1.0);

            double width = starCanvas.getWidth();
            double height = starCanvas.getHeight();
            if (x >= -size && x <= width + size && y >= -size && y <= height + size) {

                Image sunImage = imageService.loadImage("sun");

                if (sunImage != null) {
                    gc.setFill(Color.rgb(255, 255, 150, 0.3));
                    gc.fillOval(x - size * 1.5, y - size * 1.5, size * 3, size * 3);
                    gc.drawImage(sunImage, x - size/2, y - size/2, size, size);
                    gc.setFill(Color.rgb(255, 245, 200, 0.2));
                    gc.fillOval(x - size * 0.8, y - size * 0.8, size * 1.6, size * 1.6);
                } else {
                    gc.setFill(Color.rgb(255, 255, 150, 0.4));
                    gc.fillOval(x - size * 1.5, y - size * 1.5, size * 3, size * 3);
                    gc.setFill(Color.rgb(255, 255, 100));
                    gc.fillOval(x - size/2, y - size/2, size, size);
                    gc.setFill(Color.rgb(255, 245, 200, 0.6));
                    gc.fillOval(x - size/3, y - size/3, size/1.5, size/1.5);
                }

                gc.setFill(Color.rgb(255, 255, 150));
                gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 12));
                gc.fillText("☀ Sun", x + size/2 + 5, y - size/2);
            }
        }
    } else {
        drawHorizonIndicator("☀ Sun (below horizon)",
                           Color.rgb(255, 200, 100),
                           currentSunPosition.getAltitude());
    }
}
```
</details>

### Replace drawMoon() - Around Line 1542

<details>
<summary>Click to expand code</summary>

```java
private void drawMoon() {
    if (!showMoon || currentMoonPosition == null || projection == null) {
        return;
    }

    if (currentMoonPosition.isVisible()) {
        double[] coords = projection.raDecToScreen(currentMoonPosition.getRa(), currentMoonPosition.getDec());
        if (coords != null) {
            double x = coords[0];
            double y = coords[1];
            double size = imageService.calculateAutoSize("moon", 1.0, 0.00257);
            double illumination = currentMoonPosition.getIllumination();

            double width = starCanvas.getWidth();
            double height = starCanvas.getHeight();
            if (x >= -size && x <= width + size && y >= -size && y <= height + size) {

                Image moonImage = imageService.loadImage("moon");

                if (moonImage != null) {
                    gc.setFill(Color.rgb(200, 200, 220, 0.2));
                    gc.fillOval(x - size * 0.9, y - size * 0.9, size * 1.8, size * 1.8);
                    gc.drawImage(moonImage, x - size/2, y - size/2, size, size);

                    if (illumination < 0.98) {
                        gc.save();
                        gc.setFill(Color.rgb(10, 10, 35, 0.7));
                        if (illumination < 0.5) {
                            double shadowWidth = size * (1 - illumination * 2);
                            gc.fillOval(x - size/2 + size - shadowWidth, y - size/2, shadowWidth, size);
                        } else {
                            double shadowWidth = size * (2 - illumination * 2);
                            gc.fillOval(x - size/2, y - size/2, shadowWidth, size);
                        }
                        gc.restore();
                    }
                } else {
                    gc.setFill(Color.rgb(220, 220, 220, 0.9));
                    gc.fillOval(x - size/2, y - size/2, size, size);

                    if (illumination < 0.98) {
                        gc.setFill(Color.rgb(50, 50, 80, 0.8));
                        if (illumination < 0.5) {
                            double shadowWidth = size * (1 - illumination * 2);
                            gc.fillOval(x - size/2 + size - shadowWidth, y - size/2, shadowWidth, size);
                        } else {
                            double shadowWidth = size * (2 - illumination * 2);
                            gc.fillOval(x - size/2, y - size/2, shadowWidth, size);
                        }
                    }

                    gc.setFill(Color.rgb(200, 200, 220, 0.3));
                    gc.fillOval(x - size * 0.8, y - size * 0.8, size * 1.6, size * 1.6);
                }

                gc.setFill(Color.rgb(200, 200, 220));
                gc.setFont(javafx.scene.text.Font.font("Arial", 10));
                String label = String.format("%s %s (%d%%)",
                    currentMoonPosition.getPhaseEmoji(),
                    currentMoonPosition.getPhaseName(),
                    currentMoonPosition.getIlluminationPercent());
                gc.fillText(label, x + size/2 + 5, y - size/2);
            }
        }
    } else {
        drawHorizonIndicator(String.format("🌙 Moon (%s, %d%% lit) - below horizon",
                           currentMoonPosition.getPhaseName(),
                           currentMoonPosition.getIlluminationPercent()),
                           Color.rgb(180, 180, 200),
                           currentMoonPosition.getAltitude());
    }
}
```
</details>

### Replace drawPlanet() - Around Line 1305

<details>
<summary>Click to expand code</summary>

```java
private void drawPlanet(Planet planet, double x, double y) {
    double baseMagnification = planet.isHovered() ? 1.3 : 1.0;
    double distanceAU = planet.getDistance();
    double size = imageService.calculateAutoSize(planet.getName().toLowerCase(), baseMagnification, distanceAU);
    Color color = Color.web(planet.getDisplayColor());

    if (planet.getMagnitude() < 1.0 || planet.isHovered()) {
        double glowOpacity = planet.isHovered() ? 0.5 : 0.3;
        gc.setFill(Color.rgb((int)(color.getRed()*255), (int)(color.getGreen()*255),
                            (int)(color.getBlue()*255), glowOpacity));
        gc.fillOval(x - size * 1.2, y - size * 1.2, size * 2.4, size * 2.4);
    }

    if (planet.isHovered()) {
        gc.setStroke(Color.rgb(255, 255, 255, 0.8));
        gc.setLineWidth(2.0);
        gc.strokeOval(x - size * 0.7, y - size * 0.7, size * 1.4, size * 1.4);
    }

    Image planetImage = imageService.loadImage(planet.getName().toLowerCase());

    if (planetImage != null) {
        gc.drawImage(planetImage, x - size/2, y - size/2, size, size);
        if (planet.getName().equalsIgnoreCase("Saturn")) {
            drawSaturnRings(x, y, size, color);
        }
    } else {
        gc.setFill(color);
        gc.fillOval(x - size/2, y - size/2, size, size);
        if (planet.getName().equalsIgnoreCase("Saturn")) {
            drawSaturnRings(x, y, size, color);
        } else if (planet.getName().equalsIgnoreCase("Jupiter")) {
            drawJupiterBands(x, y, size, color);
        }
    }

    gc.setFill(Color.rgb(255, 255, 200));
    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.NORMAL, 10));
    String label = String.format("%s %s", planet.getSymbol(), planet.getName());
    if (planet.isDwarfPlanet()) label += " (Dwarf)";
    else if (planet.isAsteroid()) label += " (Asteroid)";
    gc.fillText(label, x + size/2 + 3, y - size/2 - 2);

    if (planet.getMagnitude() < 8.0 || planet.isHovered()) {
        gc.setFont(javafx.scene.text.Font.font("Arial", 8));
        gc.setFill(Color.rgb(200, 200, 200, 0.8));
        String info = String.format("mag %.1f, %s", planet.getMagnitude(), planet.getDistanceString());
        gc.fillText(info, x + size/2 + 3, y + size/2 + 10);

        if (planet.isHovered() && planet.getOrbitalPeriod() > 0) {
            String periodInfo = String.format("Period: %s", planet.getOrbitalPeriodString());
            gc.fillText(periodInfo, x + size/2 + 3, y + size/2 + 22);
        }
    }
}
```
</details>

---

## Step 3: Test and Add Images (15-60 minutes)

### Test Without Images First

1. **Rebuild the project in IntelliJ**
2. **Run the application**
3. ✅ Should work perfectly with fallback rendering (colored circles)
4. Check console for "Image not found" warnings (this is expected)

### Add Images for Realistic Rendering

**Quick Test** (5 minutes):
1. Go to https://www.solarsystemscope.com/textures/
2. Download "Jupiter 2K" texture
3. Save as `jupiter.png` in `gui/src/main/resources/com/dqrapps/planetarium/gui/images/celestial/`
4. Rebuild and run
5. ✨ Jupiter should now render with realistic texture!

**Full Setup** (30-60 minutes):
1. Download all planet textures from Solar System Scope
2. Rename files to match required names:
   - sun.png
   - moon.png  
   - mercury.png
   - venus.png
   - earth.png
   - mars.png
   - jupiter.png
   - saturn.png
   - uranus.png
   - neptune.png
   - pluto.png
3. Place all in the `celestial/` directory
4. Rebuild and run
5. 🎉 All celestial bodies now render realistically!

---

## 📊 What to Expect

### Console Output (Good):
```
INFO: Loaded image for jupiter: /com/.../celestial/jupiter.png
INFO: Loaded image for mars: /com/.../celestial/mars.png
WARNING: Image not found for pluto - will use fallback rendering
```

### Visual Results:

**With Images**:
- ✨ Realistic NASA/scientific photographs
- 📏 Automatically sized based on distance
- 🌍 Beautiful, professional appearance
- 🌙 Accurate moon phases with shadows
- ⭐ Museum-quality visuals

**Without Images**:
- ✅ Colored circles (original style)
- ✅ All functionality works
- ✅ No errors or crashes
- ✅ Can add images later

---

## 🎯 Recommended Testing Order

1. **Test without any images** - verify fallback works
2. **Add Jupiter** - largest, easiest to see
3. **Add Saturn** - verify rings render on top
4. **Add Moon** - verify phase shadows
5. **Add Sun** - verify glow effects
6. **Add remaining planets** - complete the system

---

## 🔧 Troubleshooting

### Images Not Showing?
- ✅ Check file names are **exactly** lowercase
- ✅ Verify files are PNG format
- ✅ Check console for "Image not found" warnings
- ✅ Ensure files are in correct directory

### Images Blurry?
- ✅ Download 2K textures (2048×2048)
- ✅ Don't upscale low-resolution images
- ✅ Use PNG format (not JPG)

### Sizes Wrong?
- ✅ Edit `CelestialImageService.java`
- ✅ Modify `defaultSizes` map
- ✅ Adjust `calculateAutoSize()` parameters

---

## 📚 More Information

- **CELESTIAL_IMAGES_GUIDE.md** - Detailed image download instructions
- **INTEGRATE_IMAGES.md** - Complete integration reference
- **REALISTIC_IMAGES_SUMMARY.md** - Full feature overview

---

## ✅ Success Checklist

- [ ] Added `CelestialImageService.java` to project
- [ ] Added import and field to `PlotController`
- [ ] Initialized service in `initialize()`
- [ ] Replaced `drawSun()` method
- [ ] Replaced `drawMoon()` method
- [ ] Replaced `drawPlanet()` method
- [ ] Rebuilt project without errors
- [ ] Tested with no images (works!)
- [ ] Added at least one test image
- [ ] Verified image loads correctly
- [ ] Checked console output
- [ ] Tested auto-sizing with zoom

---

## 🎉 You're Done!

Your Planetarium now has **museum-quality realistic visuals** with **intelligent auto-sizing**!

### Next Steps:
- Download more images for complete coverage
- Fine-tune sizes to your preference
- Show off your beautiful planetarium!
- Share screenshots with others

**Total Time**: 15-20 minutes (integration) + 30-60 minutes (images)
**Result**: Stunning, professional astronomy application!
