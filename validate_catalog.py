#!/usr/bin/env python3
"""
Validate the stars_sao_fk5.json file for correctness.
"""

import json
import sys

def validate_star_catalog(filename):
    """Validate the star catalog JSON file."""

    print(f"Validating {filename}...")

    try:
        with open(filename, 'r') as f:
            data = json.load(f)
    except Exception as e:
        print(f"Error loading JSON: {e}")
        return False

    # Check structure
    if 'objects' not in data:
        print("Error: Missing 'objects' key")
        return False

    stars = data['objects']
    print(f"Total stars: {len(stars)}")

    # Validate each star
    issues = 0
    for i, star in enumerate(stars):
        # Check required fields
        required = ['ra', 'dec', 'mag', 'name']
        for field in required:
            if field not in star:
                print(f"Star {i}: Missing required field '{field}'")
                issues += 1

        # Check coordinate ranges
        if 'ra' in star:
            ra = star['ra']
            if ra < 0 or ra >= 24:
                print(f"Star {i}: RA out of range: {ra}")
                issues += 1

        if 'dec' in star:
            dec = star['dec']
            if dec < -90 or dec > 90:
                print(f"Star {i}: Dec out of range: {dec}")
                issues += 1

        # Check magnitude range
        if 'mag' in star:
            mag = star['mag']
            if mag < -2 or mag > 15:
                print(f"Star {i}: Magnitude suspicious: {mag}")
                issues += 1

    # Statistics
    mags = [s['mag'] for s in stars if 'mag' in s]
    names = [s for s in stars if s.get('name', '').strip()]
    spectral = [s for s in stars if 'spectralType' in s]
    sao_nums = [s for s in stars if 'sao' in s]
    hd_nums = [s for s in stars if 'hd' in s]
    fk5_nums = [s for s in stars if 'fk5' in s]

    print(f"\nStatistics:")
    print(f"Magnitude range: {min(mags):.2f} to {max(mags):.2f}")
    print(f"Stars with names: {len(names)} ({100*len(names)/len(stars):.1f}%)")
    print(f"Stars with spectral types: {len(spectral)} ({100*len(spectral)/len(stars):.1f}%)")
    print(f"Stars with SAO numbers: {len(sao_nums)} ({100*len(sao_nums)/len(stars):.1f}%)")
    print(f"Stars with HD numbers: {len(hd_nums)} ({100*len(hd_nums)/len(stars):.1f}%)")
    print(f"Stars with FK5 numbers: {len(fk5_nums)} ({100*len(fk5_nums)/len(stars):.1f}%)")

    # Famous stars check
    famous_stars = ['SIRIUS', 'VEGA', 'ARCTURUS', 'CAPELLA', 'RIGEL', 'PROCYON', 'BETELGEUSE', 'POLARIS', 'ANTARES', 'DENEB']
    found_famous = []
    for star in stars:
        name = star.get('name', '')
        for famous in famous_stars:
            if famous in name.upper():
                found_famous.append(famous)

    print(f"\nFamous stars found: {len(found_famous)}/{len(famous_stars)}")
    print(f"Found: {', '.join(sorted(set(found_famous)))}")

    if issues == 0:
        print(f"\n✅ Validation PASSED - No issues found!")
        return True
    else:
        print(f"\n❌ Validation FAILED - {issues} issues found!")
        return False

if __name__ == "__main__":
    filename = "/Users/RomneyDQ/projects/dqr/planetarium/gui/stars_sao_fk5.json"
    success = validate_star_catalog(filename)
    sys.exit(0 if success else 1)
