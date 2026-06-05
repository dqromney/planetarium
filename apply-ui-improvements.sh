#!/bin/bash

# Planetarium UI Improvements Application Script
# This script backs up your original files and applies the improved UI

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GUI_RESOURCES="$SCRIPT_DIR/gui/src/main/resources/com/dqrapps/planetarium/gui"

echo "🚀 Planetarium UI Improvements Installer"
echo "=========================================="
echo ""

# Check if improved files exist
if [ ! -f "$GUI_RESOURCES/styles-improved.css" ]; then
    echo "❌ Error: Improved files not found!"
    echo "   Expected location: $GUI_RESOURCES/styles-improved.css"
    exit 1
fi

echo "✅ Found improved UI files"
echo ""

# Ask user for confirmation
read -p "🤔 Do you want to apply UI improvements? (y/n) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Operation cancelled"
    exit 0
fi

echo ""
echo "📦 Creating backups..."

# Create backup directory with timestamp
BACKUP_DIR="$SCRIPT_DIR/ui-backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

# Backup original files
if [ -f "$GUI_RESOURCES/styles.css" ]; then
    cp "$GUI_RESOURCES/styles.css" "$BACKUP_DIR/styles-original.css"
    echo "   ✓ Backed up styles.css"
fi

if [ -f "$GUI_RESOURCES/plot.fxml" ]; then
    cp "$GUI_RESOURCES/plot.fxml" "$BACKUP_DIR/plot-original.fxml"
    echo "   ✓ Backed up plot.fxml"
fi

if [ -f "$GUI_RESOURCES/config.fxml" ]; then
    cp "$GUI_RESOURCES/config.fxml" "$BACKUP_DIR/config-original.fxml"
    echo "   ✓ Backed up config.fxml"
fi

echo ""
echo "💾 Backup saved to: $BACKUP_DIR"
echo ""

# Apply improvements
echo "🎨 Applying improvements..."

cp "$GUI_RESOURCES/styles-improved.css" "$GUI_RESOURCES/styles.css"
echo "   ✓ Applied improved styles"

cp "$GUI_RESOURCES/plot-improved.fxml" "$GUI_RESOURCES/plot.fxml"
echo "   ✓ Applied improved plot layout"

cp "$GUI_RESOURCES/config-improved.fxml" "$GUI_RESOURCES/config.fxml"
echo "   ✓ Applied improved config layout"

echo ""
echo "✨ UI Improvements Applied Successfully!"
echo ""
echo "📝 Next steps:"
echo "   1. Rebuild the project in IntelliJ"
echo "   2. Run the application to see the improvements"
echo "   3. Check UI_IMPROVEMENTS.md for details"
echo ""
echo "🔄 To restore original UI, run:"
echo "   ./restore-original-ui.sh $BACKUP_DIR"
echo ""
echo "✅ Done!"
