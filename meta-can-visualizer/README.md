# meta-can-visualizer

Qt6 QML CAN Data Visualization Application for Raspberry Pi 4B

## Overview

This Yocto meta-layer provides a Qt6 QML application that visualizes CAN bus detection data from the SocketCAN interface. The application displays real-time charts showing:

1. **Current Value** - Single bar chart showing the current detection value
2. **30-Minute Averages** - 12 bars showing average detection values for each 30-minute period over the last 6 hours
3. **Threshold Exceedances** - 12 bars showing the count of readings ≥10 for each 30-minute period over the last 6 hours

## Features

- **Real-time Monitoring**: Monitors `/var/tmp/audio_detection` file using Qt's QFileSystemWatcher
- **Time-Series Analysis**: Maintains 6+ hours of historical data for statistical calculations
- **Fullscreen EGLFS**: Runs in fullscreen mode using EGLFS backend (no window manager required)
- **Auto-Start**: Configured with SysVinit script to start automatically on boot
- **Low Resource Usage**: Efficient C++ backend with QML frontend
- **Color-Coded Charts**: Dark theme with color-coded bars for easy visualization

## Architecture

### C++ Backend (DataCollector)
- Monitors data file for changes using inotify (via QFileSystemWatcher)
- Maintains time-series buffer of detection values with timestamps
- Calculates statistics:
  - Current value (latest reading)
  - 30-minute averages for 12 periods (6 hours)
  - Threshold exceedance counts (≥10) for 12 periods
- Exposes data to QML via Q_PROPERTY and signals

### QML Frontend
- Three ChartView components with BarSeries
- Auto-updates when backend signals data changes
- Styled with dark theme and gradient colors
- Responsive layout that scales to display resolution

## Dependencies

- **qtbase** - Qt6 core libraries
- **qtdeclarative** - QML runtime and QtQuick
- **qtcharts** - QtCharts module for data visualization
- **mesa** - OpenGL ES 2.0 support for rendering
- **can-server** - CAN receiver application (provides data file)

## Installation

This layer is integrated into the Yocto build system via `build.sh`:

```bash
./build.sh
```

The build script will:
1. Add `meta-can-visualizer` layer to bblayers.conf
2. Add `can-visualizer qtcharts` to IMAGE_INSTALL
3. Build the Qt6 application with CMake
4. Install binary to `/usr/bin/can-visualizer`
5. Install init script to `/etc/init.d/can-visualizer`

## Usage

### Automatic Start
The application starts automatically on boot via SysVinit at priority 98 (after can-server at 99).

### Manual Control
```bash
# Start the visualizer
/etc/init.d/can-visualizer start

# Stop the visualizer
/etc/init.d/can-visualizer stop

# Restart the visualizer
/etc/init.d/can-visualizer restart

# Check status
/etc/init.d/can-visualizer status
```

### Runtime Configuration
The init script sets these environment variables:
```bash
QT_QPA_PLATFORM=eglfs                # Use EGLFS backend
QT_QPA_EGLFS_ALWAYS_SET_MODE=1       # Force display mode
QT_QPA_EGLFS_KMS_ATOMIC=1            # Use atomic KMS
QT_LOGGING_RULES="*.debug=false"     # Disable debug logs
```

## File Structure

```
meta-can-visualizer/
├── conf/
│   └── layer.conf                   # Layer configuration
├── recipes-apps/
│   └── can-visualizer/
│       ├── can-visualizer_1.0.bb    # Yocto recipe
│       └── files/
│           ├── CMakeLists.txt       # CMake build configuration
│           ├── main.cpp             # Qt application entry point
│           ├── datacollector.h      # Data collector header
│           ├── datacollector.cpp    # Data collector implementation
│           ├── main.qml             # QML frontend
│           ├── can-visualizer.init  # SysVinit script
│           └── LICENSE              # MIT License
└── README.md                        # This file
```

## Data Source

The application reads from `/var/tmp/audio_detection`, which is written by the `can_server_receiver` application. The file contains a single integer value representing the current detection count.

Data flow:
```
CAN Bus → SocketCAN (can0) → can_server_receiver → /var/tmp/audio_detection → can-visualizer → Display
```

## Configuration

### Threshold Value
The threshold for counting exceedances is set to 10 in `datacollector.h`:
```cpp
static constexpr int THRESHOLD_VALUE = 10;
```

### Time Periods
The application tracks 12 periods of 30 minutes each (6 hours total):
```cpp
static constexpr int NUM_PERIODS = 12;
static constexpr int PERIOD_MINUTES = 30;
```

### Display Resolution
Default resolution is 800x480 (typical for embedded displays), but the application scales to the native display resolution.

## Testing

### Manual Testing
Write test values to the data file:
```bash
echo 5 > /var/tmp/audio_detection    # Write value 5
echo 12 > /var/tmp/audio_detection   # Write value 12 (exceeds threshold)
```

### Verify Charts Update
- Current value bar should update immediately
- After 30+ minutes, check that averages are calculated correctly
- After 6+ hours, verify all 12 period bars are populated

### Debug Logging
Enable Qt debug logging:
```bash
export QT_LOGGING_RULES="*.debug=true"
/usr/bin/can-visualizer
```

## Integration with Existing System

The visualizer coexists with the LED HAT application:
- Both monitor the same `/var/tmp/audio_detection` file
- LED HAT provides immediate visual feedback (color-coded LEDs)
- Qt6 visualizer provides detailed historical analysis (charts)

### Service Startup Order
1. S40can0 - Initialize CAN interface
2. S99can-server - Start CAN receiver
3. **S98can-visualizer** - Start Qt6 visualizer
4. ledhat - Start LED HAT controller

## Troubleshooting

### Display Not Showing
1. Check HDMI connection
2. Verify EGLFS environment variables are set
3. Check Qt debug logs: `journalctl -u can-visualizer`

### Charts Not Updating
1. Verify `/var/tmp/audio_detection` file exists and is being written
2. Check can-server is running: `ps aux | grep can_server_receiver`
3. Check file watcher: Enable debug logging to see file change events

### Build Errors
1. Verify qtcharts is in DEPENDS and RDEPENDS
2. Check Qt6 is properly configured in build.sh
3. Verify all source files are listed in SRC_URI

## License

MIT License - See LICENSE file for details

## Author

AESD Final Project - CAN Data Visualization System
