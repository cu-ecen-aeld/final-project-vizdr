#!/bin/bash

# Visual Layout Editor for CAN Visualizer
# Opens the QML file in Qt Creator and runs a live preview

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DESIGNER_QML="$SCRIPT_DIR/main_designer.qml"
BUILD_DIR="$SCRIPT_DIR/designer-preview/build"

echo "======================================"
echo "CAN Visualizer Layout Editor"
echo "======================================"
echo ""

# Copy QML file to build directory for preview
cp "$DESIGNER_QML" "$BUILD_DIR/"

echo "Opening Qt Creator with main_designer.qml..."
echo ""

# Launch Qt Creator WITHOUT Qt 6.7.2 environment variables (to avoid snap conflicts)
qtcreator "$DESIGNER_QML" &
QTCREATOR_PID=$!

echo "Waiting for Qt Creator to start..."
sleep 3

# Now set Qt 6.7.2 environment for the preview tool
QT_DIR="${HOME}/Qt/6.7.2/gcc_64"
export LD_LIBRARY_PATH="$QT_DIR/lib:$LD_LIBRARY_PATH"
export QML2_IMPORT_PATH="$QT_DIR/qml"
export QT_PLUGIN_PATH="$QT_DIR/plugins"
export QT_QPA_PLATFORM_PLUGIN_PATH="$QT_DIR/plugins/platforms"

echo ""
echo "Starting live preview window..."
echo ""
echo "WORKFLOW:"
echo "  1. Edit main_designer.qml in Qt Creator"
echo "  2. Save the file (Ctrl+S)"
echo "  3. Close and restart this script to see changes"
echo ""
echo "COMMON ADJUSTMENTS:"
echo "  - Window size: Change 'width' and 'height' in Window {}"
echo "  - Chart spacing: Change 'spacing' in ColumnLayout {}"
echo "  - Chart heights: Add 'Layout.preferredHeight: 160' to each ChartView"
echo "  - Colors: Modify 'color', 'backgroundColor', etc."
echo ""
echo "When satisfied, copy your changes from main_designer.qml to main.qml"
echo ""
echo "Press Ctrl+C to close the preview"
echo "======================================"
echo ""

# Run preview
cd "$BUILD_DIR"
./designer-preview

# Cleanup
echo ""
echo "Preview closed."
