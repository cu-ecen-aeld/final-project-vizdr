#!/bin/bash
# Wrapper script to run can-visualizer with Qt 6.7.2

# Qt 6.7.2 installation path
QT_DIR="${HOME}/Qt/6.7.2/gcc_64"

# Check if Qt 6.7.2 is installed
if [ ! -d "$QT_DIR" ]; then
    echo "Error: Qt 6.7.2 not found at $QT_DIR"
    echo "Please install Qt 6.7.2 first."
    exit 1
fi

# Build directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build/Qt6.7.2-Debug"

# Check if build exists
if [ ! -f "$BUILD_DIR/can-visualizer" ]; then
    echo "Error: can-visualizer not built yet!"
    echo "Please build with Qt 6.7.2 first:"
    echo ""
    echo "  cd $SCRIPT_DIR"
    echo "  mkdir -p build/Qt6.7.2-Debug && cd build/Qt6.7.2-Debug"
    echo "  cmake ../.. -DCMAKE_PREFIX_PATH=$QT_DIR"
    echo "  make"
    exit 1
fi

# Check if data file exists
if [ ! -f "/var/tmp/audio_detection" ]; then
    echo "Warning: /var/tmp/audio_detection does not exist"
    echo "Creating with default value 7..."
    echo "7" | sudo tee /var/tmp/audio_detection > /dev/null
fi

echo "Starting CAN Visualizer (Qt 6.7.2)..."
echo "Data file: /var/tmp/audio_detection"
echo "Current value: $(cat /var/tmp/audio_detection 2>/dev/null || echo 'N/A')"
echo ""

cd "$BUILD_DIR"

# Run with Qt 6.7.2 environment
export LD_LIBRARY_PATH="$QT_DIR/lib:$LD_LIBRARY_PATH"
export QML2_IMPORT_PATH="$QT_DIR/qml"
export QT_PLUGIN_PATH="$QT_DIR/plugins"
export QT_QPA_PLATFORM_PLUGIN_PATH="$QT_DIR/plugins/platforms"

./can-visualizer "$@"
