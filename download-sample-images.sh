#!/bin/bash

# Sample Celestial Images Downloader
# Downloads a few sample images to test the realistic rendering system
#
# NOTE: These are lower quality samples for testing.
# For best results, follow CELESTIAL_IMAGES_GUIDE.md to get high-quality NASA images.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEST_DIR="$SCRIPT_DIR/gui/src/main/resources/com/dqrapps/planetarium/gui/images/celestial"

echo "🌟 Downloading Sample Celestial Images"
echo "======================================"
echo ""
echo "📁 Destination: $DEST_DIR"
echo ""

# Create destination directory
mkdir -p "$DEST_DIR"

# Check if ImageMagick is installed (needed for circular conversion)
if ! command -v convert &> /dev/null; then
    echo "⚠️  ImageMagick not found!"
    echo ""
    echo "   ImageMagick is needed to convert images to circular format."
    echo ""
    echo "   Install it with:"
    echo "   - macOS: brew install imagemagick"
    echo "   - Linux: apt-get install imagemagick"
    echo ""
    echo "   Or download images manually from:"
    echo "   https://www.solarsystemscope.com/textures/"
    echo ""
    exit 1
fi

echo "✅ ImageMagick found"
echo ""

# Function to create a circular PNG from a rectangular image
make_circular() {
    local input="$1"
    local output="$2"
    local size="$3"

    convert "$input" -resize ${size}x${size} \
        \( +clone -threshold -1 -negate -fill white -draw "circle $((size/2)),$((size/2)) $((size/2)),0" \) \
        -alpha off -compose copy_opacity -composite \
        "$output"

    if [ -f "$output" ]; then
        echo "   ✓ Created $output"
    else
        echo "   ✗ Failed to create $output"
    fi
}

echo "📥 Downloading images..."
echo ""
echo "NOTE: Automated downloads from NASA require finding current working URLs."
echo "      The best approach is to manually download from:"
echo ""
echo "      1. Solar System Scope (easiest):"
echo "         https://www.solarsystemscope.com/textures/"
echo "         - Download 2K textures"
echo "         - Already circular and ready to use"
echo ""
echo "      2. NASA SVS (highest quality):"
echo "         https://svs.gsfc.nasa.gov/"
echo "         - Search for each planet"
echo "         - Download highest resolution"
echo "         - Convert to circular with this script's helper function"
echo ""
echo "      3. NASA Solar System:"
echo "         https://solarsystem.nasa.gov/resources/"
echo "         - Filter by 'Images'"
echo "         - Select planet"
echo "         - Download and convert"
echo ""

# Create placeholder instructions file
cat > "$DEST_DIR/README.txt" << 'EOF'
Celestial Body Images Directory
================================

Place your planet, moon, and sun images here.

Required Format:
- PNG with transparent background
- 512x512 pixels recommended
- Circular (not rectangular)
- Named in lowercase

Required Files:
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

Where to Get Images:
--------------------

1. Solar System Scope (Easiest)
   URL: https://www.solarsystemscope.com/textures/
   - Download 2K textures
   - Already circular and ready to use
   - Rename files to match required names above

2. NASA Scientific Visualization Studio
   URL: https://svs.gsfc.nasa.gov/
   - Search for each planet name
   - Download highest resolution
   - Convert to circular PNG using ImageMagick

3. NASA Solar System Exploration
   URL: https://solarsystem.nasa.gov/resources/
   - Filter by "Images"
   - Search specific planet
   - Download and convert

Converting Images:
------------------

If you have rectangular planet images, convert them to circular:

convert input.jpg -resize 512x512 \
  \( +clone -threshold -1 -negate -fill white -draw "circle 256,256 256,0" \) \
  -alpha off -compose copy_opacity -composite \
  output.png

Example:
convert jupiter_nasa.jpg -resize 512x512 \
  \( +clone -threshold -1 -negate -fill white -draw "circle 256,256 256,0" \) \
  -alpha off -compose copy_opacity -composite \
  jupiter.png

Testing:
--------

After adding images:
1. Rebuild your project
2. Run the application
3. Check console for image loading messages
4. Verify planets render with images

The application works fine without images - it will use fallback rendering
(colored circles) for any missing images.

For detailed instructions, see:
- CELESTIAL_IMAGES_GUIDE.md (in project root)
- INTEGRATE_IMAGES.md (in project root)
EOF

echo "📄 Created README.txt in image directory"
echo ""

# Create a simple test image (blue circle for testing)
if command -v convert &> /dev/null; then
    echo "🎨 Creating test image (blue circle)..."

    convert -size 512x512 xc:none \
        -fill "#4169E1" -draw "circle 256,256 256,0" \
        "$DEST_DIR/test-circle.png"

    if [ -f "$DEST_DIR/test-circle.png" ]; then
        echo "   ✓ Created test-circle.png"
        echo ""
        echo "   This is a simple blue circle for testing."
        echo "   Rename it to any planet name to test image rendering:"
        echo "   mv test-circle.png jupiter.png"
    fi
fi

echo ""
echo "✅ Setup Complete!"
echo ""
echo "📝 Next Steps:"
echo ""
echo "   1. Download images from one of the sources mentioned above"
echo "   2. Place PNG files in: $DEST_DIR"
echo "   3. Ensure files are named correctly (lowercase):"
echo "      sun.png, moon.png, jupiter.png, etc."
echo "   4. Rebuild your project"
echo "   5. Run the application to see realistic images!"
echo ""
echo "📖 See CELESTIAL_IMAGES_GUIDE.md for detailed instructions"
echo ""
echo "💡 Tip: Start with just Jupiter or Saturn to test the system"
echo "   Then add more images as you have time"
echo ""
