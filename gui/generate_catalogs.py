#!/usr/bin/env python3
"""
Generate synthetic star catalogs of various sizes for testing.
Creates realistic star distributions with proper RA/Dec coordinates.
"""

import json
import random
import math

# Star names for variety
STAR_PREFIXES = ["Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta",
                 "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi"]
CONSTELLATIONS = ["Andromedae", "Aquarii", "Arietis", "Bootis", "Cancri", "Capricorni",
                  "Cassiopeiae", "Centauri", "Cephei", "Ceti", "Cygni", "Draconis",
                  "Geminorum", "Leonis", "Librae", "Lyrae", "Orionis", "Pegasi",
                  "Persei", "Piscium", "Sagittarii", "Scorpii", "Tauri", "Ursae Majoris",
                  "Ursae Minoris", "Virginis"]
SPECTRAL_TYPES = ["O5", "B0", "B5", "A0", "A5", "F0", "F5", "G0", "G5", "K0", "K5", "M0", "M5"]

def generate_star(index):
    """Generate a single synthetic star with realistic properties."""
    # RA: 0-24 hours, uniform distribution
    ra = random.uniform(0, 24)

    # Dec: -90 to +90 degrees, but with more stars near equator (realistic distribution)
    # Use sine distribution to concentrate stars near celestial equator
    dec_uniform = random.uniform(-1, 1)
    dec = math.degrees(math.asin(dec_uniform))

    # Magnitude: Most stars are dim, few are bright (follows logarithmic distribution)
    # Magnitude 0 is brightest, 6 is dimmest visible to naked eye
    mag = random.gauss(4.0, 1.5)  # Mean 4.0, stddev 1.5
    mag = max(-1.5, min(6.5, mag))  # Clamp to reasonable range

    # Name: Use Greek letter + constellation pattern
    if random.random() > 0.7:  # 30% have names
        name = f"{random.choice(STAR_PREFIXES)} {random.choice(CONSTELLATIONS)} {index}"
    else:
        name = f"Star {index}"

    # Spectral type: Distribution follows stellar population
    # More G, K, M stars (cooler), fewer O, B stars (hotter)
    spectral_weights = [0.00003, 0.13, 0.6, 3, 7.6, 12.1, 23.4, 100, 76.5, 121, 850, 234, 1000]
    spectral_type = random.choices(SPECTRAL_TYPES, weights=spectral_weights)[0]

    return {
        "ra": round(ra, 4),
        "dec": round(dec, 2),
        "mag": round(mag, 1),
        "name": name,
        "spectralType": spectral_type
    }

def generate_catalog(size, filename):
    """Generate a star catalog of the given size."""
    print(f"Generating {size:,} stars...")

    stars = [generate_star(i) for i in range(1, size + 1)]

    # Sort by magnitude (brightest first) - more realistic
    stars.sort(key=lambda s: s["mag"])

    catalog = {
        "objects": stars
    }

    with open(filename, 'w') as f:
        json.dump(catalog, f, indent=2)

    print(f"Saved {filename} ({size:,} stars)")

    # Calculate file size
    import os
    size_kb = os.path.getsize(filename) / 1024
    print(f"  File size: {size_kb:.1f} KB")

def main():
    """Generate multiple catalog sizes."""
    print("=== Star Catalog Generator ===\n")

    catalogs = [
        (1000, "stars_1k.json"),
        (10000, "stars_10k.json"),
        (100000, "stars_100k.json")
    ]

    for size, filename in catalogs:
        generate_catalog(size, filename)
        print()

    print("Done! All catalogs generated.")
    print("\nCatalog files:")
    print("  stars.json       - 166 stars (original)")
    print("  stars_1k.json    - 1,000 stars")
    print("  stars_10k.json   - 10,000 stars")
    print("  stars_100k.json  - 100,000 stars")

if __name__ == "__main__":
    main()

