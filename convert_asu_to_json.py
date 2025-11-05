#!/usr/bin/env python3
"""
Convert ASU TSV file (FK5-SAO-HD Cross Index) to stars_sao_fk5.json format.
"""

import json
import re
import sys
from typing import Dict, List, Optional

def parse_coordinates(coord_str: str, is_ra: bool = True) -> float:
    """Parse coordinate string (h m s or d m s) to decimal degrees or hours."""
    if not coord_str or coord_str.strip() == '':
        return 0.0

    # Remove quotes and clean
    coord_str = coord_str.strip().strip('"')

    # Split by spaces (not colons)
    parts = coord_str.split()
    if len(parts) != 3:
        return 0.0

    try:
        hours_or_deg = float(parts[0])
        minutes = float(parts[1])
        seconds = float(parts[2])

        # Convert to decimal
        decimal = abs(hours_or_deg) + minutes/60.0 + seconds/3600.0

        # Handle negative coordinates (mainly for declination)
        if hours_or_deg < 0 or coord_str.startswith('-'):
            decimal = -decimal

        return decimal

    except ValueError:
        return 0.0

def clean_name(name: str) -> str:
    """Clean and format star name."""
    if not name or name.strip() == '':
        return ''

    name = name.strip()
    # Remove extra spaces
    name = re.sub(r'\s+', ' ', name)
    return name.upper()

def parse_magnitude(mag_str: str) -> float:
    """Parse magnitude string to float."""
    if not mag_str or mag_str.strip() == '':
        return 99.0  # Default for missing magnitude

    try:
        return float(mag_str.strip())
    except ValueError:
        return 99.0

def parse_spectral_type(sp_str: str) -> str:
    """Parse and clean spectral type."""
    if not sp_str or sp_str.strip() == '':
        return ''

    sp = sp_str.strip()
    # Keep only the main spectral class (O,B,A,F,G,K,M) and number
    sp_match = re.match(r'([OBAFGKM])(\d*)', sp)
    if sp_match:
        return sp_match.group(0)
    return sp

def convert_asu_to_json(input_file: str, output_file: str):
    """Convert ASU TSV file to JSON format."""

    stars = []
    line_count = 0
    data_start_found = False

    print(f"Converting {input_file} to {output_file}...")

    with open(input_file, 'r', encoding='utf-8') as f:
        for line in f:
            line_count += 1
            line = line.strip()

            # Skip empty lines and comments
            if not line or line.startswith('#') or line.startswith('FK5|'):
                continue

            # Skip header separator
            if line.startswith('----'):
                data_start_found = True
                continue

            if not data_start_found:
                continue

            # Split by | delimiter
            fields = line.split('|')
            if len(fields) < 11:
                print(f"Warning: Line {line_count} has insufficient fields: {len(fields)}")
                continue

            # Extract fields according to the format:
            # FK5|RAJ2000|pmRA|DEJ2000|pmDE|Vmag|Sp|DM|SAO|HD|name
            fk5_num = fields[0].strip()
            ra_str = fields[1].strip()
            pm_ra = fields[2].strip()
            dec_str = fields[3].strip()
            pm_dec = fields[4].strip()
            vmag_str = fields[5].strip()
            spectral = fields[6].strip()
            dm_id = fields[7].strip()
            sao_num = fields[8].strip()
            hd_num = fields[9].strip()
            name_str = fields[10].strip() if len(fields) > 10 else ''

            # Parse coordinates
            ra = parse_coordinates(ra_str, True)  # RA in hours
            dec = parse_coordinates(dec_str, False)  # Dec in degrees

            # Parse magnitude
            mag = parse_magnitude(vmag_str)

            # Clean name
            name = clean_name(name_str)

            # Parse spectral type
            sp_type = parse_spectral_type(spectral)

            # Skip if no valid coordinates
            if ra == 0.0 and dec == 0.0:
                continue

            # Skip very faint stars (mag > 11)
            if mag > 11.0:
                continue

            # Create star object with only supported fields
            star = {
                "ra": round(ra, 6),
                "dec": round(dec, 5),
                "mag": round(mag, 2),
                "name": name if name else f"FK5 {fk5_num}",
            }

            # Add optional spectral type if available
            if sp_type:
                star["spectralType"] = sp_type

            # Note: sao, hd, fk5 fields removed because Star model doesn't support them
            # These could be added later if the Star model is extended


            stars.append(star)

            if len(stars) % 100 == 0:
                print(f"Processed {len(stars)} stars...")

    # Sort by magnitude (brightest first)
    stars.sort(key=lambda x: x['mag'])

    # Create output JSON structure
    output = {
        "objects": stars
    }

    # Write JSON file
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(output, f, indent=2)

    print(f"Conversion complete!")
    print(f"Total stars: {len(stars)}")

    if stars:
        print(f"Magnitude range: {min(s['mag'] for s in stars):.2f} to {max(s['mag'] for s in stars):.2f}")
        print(f"Sample star: {stars[0]}")
    else:
        print("Warning: No stars were parsed successfully!")

    print(f"Output: {output_file}")

if __name__ == "__main__":
    input_file = "/Users/RomneyDQ/projects/dqr/planetarium/logic/src/main/resources/support/asu.tsv"
    output_file = "/Users/RomneyDQ/projects/dqr/planetarium/gui/stars_sao_fk5.json"

    try:
        convert_asu_to_json(input_file, output_file)
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)
