#!/bin/bash

# Preview QML layout for visual editing
# Usage: ./preview-layout.sh

QT_DIR="${HOME}/Qt/6.7.2/gcc_64"

export LD_LIBRARY_PATH="$QT_DIR/lib:$LD_LIBRARY_PATH"
export QML2_IMPORT_PATH="$QT_DIR/qml"
export QT_PLUGIN_PATH="$QT_DIR/plugins"
export QT_QPA_PLATFORM_PLUGIN_PATH="$QT_DIR/plugins/platforms"

echo "Starting QML Layout Designer Preview..."
echo ""

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Copy the latest main_designer.qml to build directory
echo "Loading latest main_designer.qml..."
cp "$SCRIPT_DIR/main_designer.qml" "$SCRIPT_DIR/designer-preview/build/"

echo ""
echo "Preview running. To see changes:"
echo "1. Edit: $SCRIPT_DIR/main_designer.qml"
echo "2. Save the file"
echo "3. Close this preview (Ctrl+C)"
echo "4. Run ./preview-layout.sh again"
echo ""
echo "When done, copy layout changes from main_designer.qml to main.qml"
echo ""

# Use our custom designer preview tool (includes QtWidgets support)
cd "$SCRIPT_DIR/designer-preview/build"
./designer-preview
