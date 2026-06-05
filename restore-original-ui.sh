#!/bin/bash

# Planetarium UI Restoration Script
# Restores the original UI from a backup

set -e  # Exit on error

if [ -z "$1" ]; then
    echo "❌ Error: Backup directory not specified"
    echo ""
    echo "Usage: $0 <backup-directory>"
    echo ""
    echo "Available backups:"
    ls -1dt ui-backups/*/ 2>/dev/null | head -5 || echo "  (no backups found)"
    exit 1
fi

BACKUP_DIR="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GUI_RESOURCES="$SCRIPT_DIR/gui/src/main/resources/com/dqrapps/planetarium/gui"

echo "🔄 Planetarium UI Restoration"
echo "=============================="
echo ""
echo "📂 Restoring from: $BACKUP_DIR"
echo ""

# Check if backup exists
if [ ! -d "$BACKUP_DIR" ]; then
    echo "❌ Error: Backup directory not found: $BACKUP_DIR"
    exit 1
fi

# Restore files
echo "📦 Restoring original files..."

if [ -f "$BACKUP_DIR/styles-original.css" ]; then
    cp "$BACKUP_DIR/styles-original.css" "$GUI_RESOURCES/styles.css"
    echo "   ✓ Restored styles.css"
fi

if [ -f "$BACKUP_DIR/plot-original.fxml" ]; then
    cp "$BACKUP_DIR/plot-original.fxml" "$GUI_RESOURCES/plot.fxml"
    echo "   ✓ Restored plot.fxml"
fi

if [ -f "$BACKUP_DIR/config-original.fxml" ]; then
    cp "$BACKUP_DIR/config-original.fxml" "$GUI_RESOURCES/config.fxml"
    echo "   ✓ Restored config.fxml"
fi

echo ""
echo "✅ Original UI restored successfully!"
echo ""
echo "📝 Next steps:"
echo "   1. Rebuild the project in IntelliJ"
echo "   2. Run the application"
echo ""
