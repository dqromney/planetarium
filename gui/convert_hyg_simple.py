#!/usr/bin/env python3
"""
Simple HYG Database to Planetarium JSON Converter
Uses only standard library modules.
"""

import urllib.request
import csv
import json
import io

def load_local_hyg_data(filename="hygdata_v41.csv"):
    """Load HYG data from local CSV file"""
    print(f"Loading local HYG Database from {filename}...")

    try:
        with open(filename, 'r', encoding='utf-8') as file:
            data = file.read()
        print(f"Loaded {len(data)} characters from local file")
        return data
    except Exception as e:
        print(f"Error loading local file: {e}")
        return None

def download_hyg_data():
    """Download HYG data from GitHub using urllib"""
    print("Downloading HYG Database...")
    url = "https://github.com/astronexus/HYG-Database/raw/master/hygdata_v41.csv"

    try:
        with urllib.request.urlopen(url) as response:
            data = response.read().decode('utf-8')
        print(f"Downloaded {len(data)} characters")
        return data
    except Exception as e:
        print(f"Error downloading: {e}")
        return None

def convert_hyg_to_json(csv_data, max_magnitude=6.0, max_stars=10000, output_file="stars_hyg.json"):
    """Convert HYG CSV data to planetarium JSON format"""

    stars = []

    # Parse CSV data
    csv_reader = csv.DictReader(io.StringIO(csv_data))

    processed = 0
    for row in csv_reader:
        processed += 1
        if processed % 10000 == 0:
            print(f"Processed {processed} rows, found {len(stars)} valid stars...")

        try:
            # Parse essential data
            ra = float(row['ra']) if row['ra'] else None
            dec = float(row['dec']) if row['dec'] else None
            mag = float(row['mag']) if row['mag'] else None

            # Skip if missing essential data
            if ra is None or dec is None or mag is None:
                continue

            # Apply magnitude filter
            if mag > max_magnitude:
                continue

            # RA is already in hours in HYG database - no conversion needed!
            ra_hours = ra

            # Get star name (prefer proper name, fall back to catalog numbers)
            name = None
            if row.get('proper') and row['proper'].strip():
                name = row['proper'].strip().upper()
            elif row.get('bf') and row['bf'].strip():
                name = row['bf'].strip()
            elif row.get('hr') and row['hr'].strip():
                name = f"HR{row['hr']}"
            elif row.get('hd') and row['hd'].strip():
                name = f"HD{row['hd']}"

            # Get spectral type for realistic colors
            spectral_type = row.get('spect', '').strip() if row.get('spect') else None

            # Create star object
            star = {
                "ra": round(ra_hours, 6),
                "dec": round(dec, 6),
                "mag": round(mag, 2)
            }

            # Add optional fields
            if name:
                star["name"] = name
            if spectral_type:
                star["spectralType"] = spectral_type

            stars.append(star)

            # Limit total stars
            if len(stars) >= max_stars:
                break

        except (ValueError, TypeError, KeyError) as e:
            continue  # Skip malformed rows

    # Sort by magnitude (brightest first)
    stars.sort(key=lambda s: s['mag'])

    # Create output structure matching your existing format
    output_data = {
        "catalog": f"HYG Database (magnitude ≤ {max_magnitude})",
        "source": "https://github.com/astronexus/HYG-Database",
        "starCount": len(stars),
        "objects": stars  # Using "objects" to match existing format
    }

    # Save to JSON
    with open(output_file, 'w', encoding='utf-8') as file:
        json.dump(output_data, file, indent=2, ensure_ascii=False)

    print(f"✅ Converted {len(stars)} stars to {output_file}")
    return len(stars)

def create_multiple_catalogs(csv_data):
    """Create multiple catalog sizes from HYG data"""
    catalogs = [
        {"name": "stars_1k.json", "max_mag": 4.5, "max_stars": 1000, "desc": "1,000 Brightest Stars"},
        {"name": "stars_5k.json", "max_mag": 6.0, "max_stars": 5000, "desc": "5,000 Bright Stars"},
        {"name": "stars_10k.json", "max_mag": 7.0, "max_stars": 10000, "desc": "10,000 Stars"},
        {"name": "stars_25k.json", "max_mag": 8.0, "max_stars": 25000, "desc": "25,000 Stars"},
        {"name": "stars_50k.json", "max_mag": 9.0, "max_stars": 50000, "desc": "50,000 Stars"},
        {"name": "stars_100k.json", "max_mag": 10.0, "max_stars": 100000, "desc": "100,000 Stars"},
        {"name": "stars_full.json", "max_mag": 15.0, "max_stars": 200000, "desc": "Full HYG Catalog"}
    ]

    print("\nCreating multiple catalog sizes...")
    for catalog in catalogs:
        print(f"\nProcessing {catalog['desc']}...")
        count = convert_hyg_to_json(
            csv_data,
            catalog["max_mag"],
            catalog["max_stars"],
            catalog["name"]
        )

def main():
    print("HYG Database to Planetarium JSON Converter")
    print("=" * 50)

    # Try to load local file first, then download if needed
    csv_data = load_local_hyg_data("hygdata_v41.csv")
    if not csv_data:
        print("Local file not found, trying to download...")
        csv_data = download_hyg_data()

    if not csv_data:
        print("Failed to get HYG data from local file or download")
        return

    # Check data
    lines = csv_data.count('\n')
    print(f"CSV data contains {lines} lines")

    # Create multiple catalog sizes
    create_multiple_catalogs(csv_data)

    print("\n" + "=" * 50)
    print("✅ All catalogs created successfully!")
    print("\nGenerated files:")
    print("- stars_1k.json    (1,000 brightest stars)")
    print("- stars_5k.json    (5,000 bright stars)")
    print("- stars_10k.json   (10,000 stars)")
    print("- stars_25k.json   (25,000 stars)")
    print("- stars_50k.json   (50,000 stars)")
    print("- stars_100k.json  (100,000 stars)")
    print("- stars_full.json  (Full HYG catalog)")
    print("\nYou can now load these in your planetarium application!")

if __name__ == "__main__":
    main()
