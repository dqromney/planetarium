#!/usr/bin/env python3
"""
Convert SAO-StarCatalog.csv to JSON format for the planetarium.
Converts CSV fields: name,hd,ra,dec,vmag,class
To JSON fields: name,ra,dec,mag,spectralType
"""

import csv
import json

def convert_ra_to_hours(ra_degrees):
    """Convert RA from degrees to hours (0-24)."""
    return ra_degrees / 15.0

def determine_spectral_type(class_str):
    """
    Determine spectral type from class string.
    Most entries are just 'STAR', so we'll return None for those.
    """
    if not class_str or class_str.strip() == 'STAR':
        return None

    # If it has a spectral classification, extract the first character
    class_str = class_str.strip()
    if class_str and class_str[0] in 'OBAFGKM':
        # Take first 2 characters (e.g., "G2", "M5")
        return class_str[:2] if len(class_str) >= 2 else class_str[0]

    return None

def clean_name(name):
    """Clean up star name."""
    if not name or name.strip() == '':
        return None
    name = name.strip()
    # Remove 'SAO ' prefix if present for cleaner names
    if name.startswith('SAO '):
        return name
    return name

def convert_catalog(input_file, output_file, max_stars=None):
    """
    Convert SAO CSV catalog to JSON format.

    Args:
        input_file: Path to SAO-StarCatalog.csv
        output_file: Path to output JSON file
        max_stars: Optional limit on number of stars (for testing)
    """
    stars = []

    print(f"Converting {input_file}...")

    with open(input_file, 'r') as f:
        reader = csv.DictReader(f)

        for i, row in enumerate(reader):
            if max_stars and i >= max_stars:
                break

            try:
                # Convert RA from degrees to hours
                ra_degrees = float(row['ra'])
                ra_hours = convert_ra_to_hours(ra_degrees)

                # Get declination
                dec = float(row['dec'])

                # Get magnitude
                mag = float(row['vmag'])

                # Get name (use SAO number)
                name = clean_name(row['name'])

                # Get spectral type
                spectral_type = determine_spectral_type(row['class'])

                # Create star object
                star = {
                    "ra": round(ra_hours, 4),
                    "dec": round(dec, 2),
                    "mag": round(mag, 1),
                    "name": name,
                    "spectralType": spectral_type
                }

                stars.append(star)

            except (ValueError, KeyError) as e:
                print(f"Warning: Skipping row {i+1} due to error: {e}")
                continue

    # Sort by magnitude (brightest first)
    stars.sort(key=lambda s: s["mag"])

    # Create catalog object
    catalog = {
        "objects": stars
    }

    # Write to JSON
    with open(output_file, 'w') as f:
        json.dump(catalog, f, indent=2)

    print(f"Converted {len(stars):,} stars to {output_file}")

    # Calculate file size
    import os
    size_kb = os.path.getsize(output_file) / 1024
    size_mb = size_kb / 1024
    if size_mb >= 1:
        print(f"File size: {size_mb:.1f} MB")
    else:
        print(f"File size: {size_kb:.1f} KB")

    # Show some statistics
    print(f"\nCatalog Statistics:")
    print(f"  Total stars: {len(stars):,}")
    print(f"  Brightest star: mag {min(s['mag'] for s in stars):.1f}")
    print(f"  Dimmest star: mag {max(s['mag'] for s in stars):.1f}")

    # Count stars by magnitude
    bright_stars = sum(1 for s in stars if s['mag'] < 3.0)
    visible_stars = sum(1 for s in stars if s['mag'] < 6.0)
    print(f"  Bright stars (mag < 3): {bright_stars:,}")
    print(f"  Naked-eye visible (mag < 6): {visible_stars:,}")

def main():
    import sys
    import os

    # Find the CSV file
    csv_file = "/Users/RomneyDQ/projects/dqr/planetarium/logic/src/main/resources/support/SAO-StarCatalog.csv"

    if not os.path.exists(csv_file):
        print(f"Error: Cannot find {csv_file}")
        sys.exit(1)

    print("=== SAO Star Catalog Converter ===\n")

    # Convert full catalog
    output_file = "/Users/RomneyDQ/projects/dqr/planetarium/gui/stars_sao.json"
    convert_catalog(csv_file, output_file)

    print("\n✓ Conversion complete!")
    print(f"\nTo use this catalog:")
    print(f"  1. Copy stars_sao.json to stars.json (or keep as is)")
    print(f"  2. Rebuild: mvn clean install")
    print(f"  3. Run: cd gui && mvn javafx:run")
    print(f"\nThe catalog will be available in the Catalog cycling menu.")

if __name__ == "__main__":
    main()

