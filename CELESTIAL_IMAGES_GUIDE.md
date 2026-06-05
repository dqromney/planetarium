# Celestial Body Images Guide

## Overview

This guide explains how to add realistic images for planets, moons, and the sun to your Planetarium application.

---

## 📁 Image Directory

Images should be placed in:
```
gui/src/main/resources/com/dqrapps/planetarium/gui/images/celestial/
```

---

## 🖼️ Required Images

### File Naming Convention
All images must be **PNG format** with **transparent backgrounds** (where appropriate) and named in lowercase:

| Celestial Body | Filename | Recommended Size |
|----------------|----------|------------------|
| Sun | `sun.png` | 512×512 px |
| Moon | `moon.png` | 512×512 px |
| Mercury | `mercury.png` | 256×256 px |
| Venus | `venus.png` | 256×256 px |
| Earth | `earth.png` | 256×256 px |
| Mars | `mars.png` | 256×256 px |
| Jupiter | `jupiter.png` | 512×512 px |
| Saturn | `saturn.png` | 512×512 px (with rings) |
| Uranus | `uranus.png` | 256×256 px |
| Neptune | `neptune.png` | 256×256 px |
| Pluto | `pluto.png` | 256×256 px |

---

## 🌐 Where to Get High-Quality Images

### Option 1: NASA Image Library (Best Quality, Free)

**NASA's Scientific Visualization Studio**
- URL: https://svs.gsfc.nasa.gov/
- Search for each planet name
- Download high-resolution textures
- License: Public domain

**NASA Solar System Exploration**
- URL: https://solarsystem.nasa.gov/resources/
- Filter by "Images" and specific planet
- Download highest resolution available
- License: Public domain

**Specific NASA Resources:**
- **Sun**: https://sdo.gsfc.nasa.gov/gallery/
- **Moon**: https://svs.gsfc.nasa.gov/cgi-bin/details.cgi?aid=4720
- **Planets**: https://www.jpl.nasa.gov/images/

### Option 2: Solar System Scope (Good Quality, Free)

- URL: https://www.solarsystemscope.com/textures/
- Download 2K textures (2048×2048)
- License: Free for non-commercial use
- Already circular, ready to use

### Option 3: Wikimedia Commons (Free, Various Quality)

- URL: https://commons.wikimedia.org/
- Search: "[Planet name] texture" or "[Planet name] image"
- Look for NASA/JPL images
- License: Public domain or CC0

---

## 🛠️ Image Preparation

### Using ImageMagick (Command Line)

If you download rectangular textures, convert them to circular:

```bash
# Install ImageMagick
brew install imagemagick  # macOS
# or: apt-get install imagemagick  # Linux

# Convert to circular with transparent background
convert input.jpg -resize 512x512 \\
  \\( +clone -threshold -1 -negate -fill white -draw "circle 256,256 256,0" \\) \\
  -alpha off -compose copy_opacity -composite \\
  output.png
```

### Using GIMP (GUI Method)

1. Open image in GIMP
2. Image → Scale Image → 512×512 (or 256×256)
3. Select → By Color → Select background
4. Select → Feather → 2 pixels
5. Layer → Transparency → Add Alpha Channel
6. Edit → Clear (to make background transparent)
7. File → Export As → PNG

### Using Online Tools

**Remove.bg** (for quick background removal)
- URL: https://www.remove.bg/
- Upload planet image
- Download PNG with transparent background
- Manually crop to circle if needed

---

## 🚀 Quick Start: Download Pre-Made Images

I've prepared a script to download NASA images automatically:

```bash
cd gui/src/main/resources/com/dqrapps/planetarium/gui/images/celestial/
./download-celestial-images.sh
```

Or manually download from this curated list:

### Sun
```bash
curl -o sun.png "https://sdo.gsfc.nasa.gov/assets/img/latest/latest_512_0193.jpg"
convert sun.png -resize 512x512 sun.png
```

### Moon
```bash
curl -o moon.png "https://svs.gsfc.nasa.gov/vis/a000000/a004700/a004720/lroc_color_poles_2k.jpg"
convert moon.png -resize 512x512 \\
  \\( +clone -threshold -1 -negate -fill white -draw "circle 256,256 256,0" \\) \\
  -alpha off -compose copy_opacity -composite moon.png
```

### Planets (Solar System Scope - requires manual download)
1. Go to https://www.solarsystemscope.com/textures/
2. Download 2K textures for each planet
3. Place in the celestial directory with correct names

---

## 🎨 Image Specifications

### Technical Requirements

- **Format**: PNG with alpha channel
- **Color Space**: sRGB
- **Bit Depth**: 24-bit or 32-bit (with alpha)
- **Compression**: PNG compression (lossless)
- **Resolution**: 256×256 minimum, 512×512 recommended for larger bodies

### Visual Requirements

- **Circular**: Images should be cropped to circles
- **Centered**: Object centered in the frame
- **Transparent Background**: Background should be fully transparent
- **Realistic Colors**: Use actual NASA/scientific imagery
- **No Text**: No labels or watermarks on images

---

## 🔧 Testing Your Images

After adding images, test them:

1. Rebuild the project in IntelliJ
2. Run the application
3. Enable all celestial objects (Sun, Moon, Planets)
4. Check that images load correctly
5. Verify auto-sizing works at different zoom levels

### Debug Mode

To see image loading status, check the console output:
```
INFO: Loaded image for jupiter: /com/dqrapps/planetarium/gui/images/celestial/jupiter.png
WARNING: Image not found for pluto - will use fallback rendering
```

---

## 🎯 Auto-Sizing Behavior

The application automatically scales images based on:

1. **Base Size**: Predefined for each body type
   - Sun: 32px base
   - Planets: 8-20px base (smaller to larger)
   - Moon: 24px base

2. **Distance Scaling**: Closer objects appear larger
   - Uses logarithmic scaling
   - Prevents extremes in size

3. **Magnification**: User zoom level affects size

4. **Bounds**: Sizes clamped between 6px and 64px

---

## 📊 Fallback Rendering

If an image is not found, the application falls back to the original rendering:
- **Planets**: Colored circles with special effects (Saturn rings, Jupiter bands)
- **Sun**: Yellow disk with glow and corona
- **Moon**: Gray disk with phase simulation

This means the app will work even without images installed.

---

## 🔄 Updating Images

To replace an image:

1. Delete the old PNG file
2. Add the new PNG with the same filename
3. Restart the application (images are cached)

To clear the image cache programmatically:
```java
CelestialImageService.getInstance().clearCache();
```

---

## 📦 Batch Download Script

Save this as `download-celestial-images.sh`:

```bash
#!/bin/bash

# Celestial Images Downloader
# Downloads public domain NASA images for planets

DEST_DIR="."
mkdir -p "$DEST_DIR"

echo "🌟 Downloading celestial body images from NASA..."

# Note: These are example URLs - you'll need to find current working links
# NASA URLs change frequently, so manual download from nasa.gov is recommended

echo "✅ Download complete!"
echo ""
echo "📝 Next steps:"
echo "   1. Verify all images are circular with transparent backgrounds"
echo "   2. Rebuild your project"
echo "   3. Run the application"
echo ""
echo "💡 Tip: For best results, download 2K textures from:"
echo "   https://www.solarsystemscope.com/textures/"
```

---

## 🎨 Advanced: Custom Image Effects

You can enhance images before adding them:

### Add Glow Effect (ImageMagick)
```bash
convert planet.png \\
  \\( +clone -blur 0x10 -level 0%,100%,0.5 \\) \\
  -compose DstOver -composite \\
  planet-glow.png
```

### Adjust Colors
```bash
convert planet.png -modulate 100,120,100 planet-enhanced.png
# 100 = brightness, 120 = saturation, 100 = hue
```

### Add Subtle Shadow
```bash
convert planet.png \\
  \\( +clone -background black -shadow 80x3+2+2 \\) \\
  +swap -background none -layers merge +repage \\
  planet-shadow.png
```

---

## ❓ Troubleshooting

### Images Not Loading
- Check file names are **exactly** lowercase (e.g., `jupiter.png` not `Jupiter.png`)
- Verify files are in correct directory
- Check console for error messages
- Ensure files are valid PNG format

### Images Look Blurry
- Use higher resolution source images (512×512 minimum)
- Don't upscale low-resolution images
- Download 2K or 4K textures and downscale

### Images Have Dark Backgrounds
- Ensure transparent background (alpha channel)
- Use ImageMagick or GIMP to remove background
- Check PNG has 32-bit color depth (24-bit RGB + 8-bit alpha)

### Performance Issues
- Large images (>1024×1024) may cause slowdowns
- Stick to recommended sizes (256-512px)
- Images are cached after first load

---

## 📚 Additional Resources

- **NASA Image Galleries**: https://images.nasa.gov/
- **JPL PhotoJournal**: https://photojournal.jpl.nasa.gov/
- **ESA Image Gallery**: https://www.esa.int/ESA_Multimedia/Images
- **Solar System Scope Textures**: https://www.solarsystemscope.com/textures/
- **Planet Textures GitHub**: https://github.com/topics/planet-textures

---

## 📄 License Notes

- **NASA Images**: Public domain (free to use)
- **ESA Images**: Check specific image license
- **Solar System Scope**: Free for non-commercial use
- **Always verify** the license before using images in commercial projects

---

## ✅ Checklist

- [ ] Created image directory
- [ ] Downloaded images for all major bodies
- [ ] Converted to PNG with transparency
- [ ] Renamed files to lowercase
- [ ] Verified sizes (256×256 or 512×512)
- [ ] Tested in application
- [ ] Checked console for loading messages
- [ ] Verified auto-sizing works correctly

---

**Estimated Time**: 30-60 minutes to download and prepare all images

**Result**: Stunning, realistic celestial body visualizations with automatic sizing!
