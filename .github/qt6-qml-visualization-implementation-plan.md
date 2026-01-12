# Qt6 QML CAN Data Visualization Application - Implementation Plan

**Prompt:**
create plan to integrate qt6 qml app, which listens the received from the Socket_CAN_Receiver value and provides visualized by column charts (or similar) for the current value, for the averaged value and for the number of exceedings of the threshold over last halh of hour and for every next half of hour  within the last 6 hours

## Overview
Create a Qt6 QML application for the Raspberry Pi 4B receiver board that visualizes CAN bus detection data using column charts. The application will display:
1. **Current Value** - Real-time detection count from CAN bus (single bar)
2. **30-Minute Averages** - Average detection value for each 30-minute period over the last 6 hours (12 bars)
3. **Threshold Exceedances** - Count of times the value exceeded threshold (>=10) for each 30-minute period over the last 6 hours (12 bars)

## User Requirements Summary
- **Data Source**: Read from existing `/var/tmp/audio_detection` file (file-based IPC)
- **Data Processing**: QML app calculates all statistics (averages, threshold counts)
- **Threshold Value**: >=10 (values 10 and above count as threshold exceedances)
- **Display Mode**: Fullscreen EGLFS mode (embedded, no window manager)
- **Chart Type**: Column/bar charts for visualization

## Architecture

### Data Flow
```
CAN Bus (MCP2515)
    ↓
SocketCAN (can0)
    ↓
can_server_receiver
    ↓
/var/tmp/audio_detection (single integer value)
    ↓
Qt6 QML App (FileWatcher/QFileSystemWatcher)
    ↓
Data Buffer (stores timestamped values)
    ↓
Statistics Calculator (C++ backend)
    ↓
QML Charts (QtCharts.BarSeries)
```

### Application Components

**C++ Backend (`DataCollector` class):**
- Monitors `/var/tmp/audio_detection` using `QFileSystemWatcher`
- Reads detection values when file changes
- Maintains time-series buffer (stores value + timestamp)
- Calculates current value, 30-min rolling average
- Calculates threshold exceedance counts for 12x 30-min periods (6 hours)
- Exposes data to QML via Q_PROPERTY and signals

**QML Frontend:**
- Main window (fullscreen, 800x480 or native RPi display resolution)
- Three BarChart/ColumnSeries displays:
  1. Single bar for current value (0-15 scale, real-time)
  2. 12 bars showing 30-minute averages per period over 6 hours (0-15 scale)
  3. 12 bars showing threshold exceedances per 30-min period over 6 hours (count scale)
- Auto-updating when new data arrives
- Styled with appropriate colors (matching LED palette if possible)

## Implementation Steps

### 1. Create Yocto Meta-Layer Structure

**Directory**: `/media/vladimir/SanDisk256/Assignment-Final-Project-AELD/meta-can-visualizer/`

Structure:
```
meta-can-visualizer/
├── conf/
│   └── layer.conf
├── recipes-apps/
│   └── can-visualizer/
│       ├── can-visualizer_1.0.bb
│       └── files/
│           ├── CMakeLists.txt
│           ├── main.cpp
│           ├── datacollector.h
│           ├── datacollector.cpp
│           ├── main.qml
│           ├── can-visualizer.init
│           └── LICENSE (MIT)
└── README.md
```

**Critical Files**:
- [meta-can-visualizer/conf/layer.conf](meta-can-visualizer/conf/layer.conf) - Layer configuration
- [meta-can-visualizer/recipes-apps/can-visualizer/can-visualizer_1.0.bb](meta-can-visualizer/recipes-apps/can-visualizer/can-visualizer_1.0.bb) - Recipe

### 2. Create Layer Configuration

**File**: `meta-can-visualizer/conf/layer.conf`

Content:
```bitbake
BBPATH .= ":${LAYERDIR}"
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
            ${LAYERDIR}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "can-visualizer"
BBFILE_PATTERN_can-visualizer := "^${LAYERDIR}/"
BBFILE_PRIORITY_can-visualizer = "8"

LAYERSERIES_COMPAT_can-visualizer = "kirkstone"
LAYERDEPENDS_can-visualizer = "core qt6"
```

### 3. Create Yocto Recipe

**File**: `meta-can-visualizer/recipes-apps/can-visualizer/can-visualizer_1.0.bb`

Key sections:
```bitbake
SUMMARY = "Qt6 QML CAN Data Visualization Application"
DESCRIPTION = "Visualizes CAN bus detection data with charts showing current value, averages, and threshold exceedances"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://CMakeLists.txt \
           file://main.cpp \
           file://datacollector.h \
           file://datacollector.cpp \
           file://main.qml \
           file://can-visualizer.init \
           file://LICENSE"

S = "${WORKDIR}"
PV = "1.0"

DEPENDS = "qtbase qtdeclarative qtcharts"

inherit cmake qt6-cmake pkgconfig update-rc.d

INITSCRIPT_NAME = "can-visualizer"
INITSCRIPT_PARAMS = "defaults 98"

EXTRA_OECMAKE = "-DQT_HOST_PATH=${RECIPE_SYSROOT_NATIVE}${prefix_native}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/can-visualizer ${D}${bindir}/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/can-visualizer.init ${D}${sysconfdir}/init.d/can-visualizer
}

FILES:${PN} += "\
    ${bindir}/can-visualizer \
    ${sysconfdir}/init.d/can-visualizer \
"

RDEPENDS:${PN} += "qtbase qtdeclarative qtcharts"
```

### 4. Create CMake Build Configuration

**File**: `meta-can-visualizer/recipes-apps/can-visualizer/files/CMakeLists.txt`

Key requirements:
- CMake 3.16+ (matches Qt6 requirements)
- Find packages: Qt6Core, Qt6Gui, Qt6Qml, Qt6Quick, Qt6Charts
- Set CMAKE_AUTOMOC, CMAKE_AUTORCC (for Qt meta-object compilation)
- Add QML resources using qt_add_qml_module or qt6_add_resources
- Link executable with Qt6::Core Qt6::Gui Qt6::Qml Qt6::Quick Qt6::Charts
- Install binary to CMAKE_INSTALL_BINDIR

### 5. Implement C++ Backend (DataCollector)

**Files**:
- `datacollector.h` - Header with Q_OBJECT, signals, slots, properties
- `datacollector.cpp` - Implementation

**Key Responsibilities**:
1. **File Monitoring**: Use QFileSystemWatcher to monitor `/var/tmp/audio_detection`
2. **Data Reading**: Read integer value from file on change (QFile::readAll + QString::toInt)
3. **Time-Series Buffer**: Store data points as `struct DataPoint { int value; QDateTime timestamp; }`
4. **Buffer Management**: Keep data for 6+ hours, prune old entries periodically
5. **Current Value**: Latest value from file
6. **30-Min Averages**: For each of 12 30-minute periods (0-30min ago, 30-60min ago, ..., 330-360min ago), calculate the average of all values in that period
7. **Threshold Exceedances**: For each of 12 30-minute periods (0-30min ago, 30-60min ago, ..., 330-360min ago), count how many readings had value >= 10

**Q_PROPERTY Exports to QML**:
```cpp
Q_PROPERTY(int currentValue READ currentValue NOTIFY currentValueChanged)
Q_PROPERTY(QVariantList averageValues READ averageValues NOTIFY averageValuesChanged)
Q_PROPERTY(QVariantList thresholdCounts READ thresholdCounts NOTIFY thresholdCountsChanged)
```

**Signals**:
- `void currentValueChanged()`
- `void averageValuesChanged()`
- `void thresholdCountsChanged()`
- `void dataUpdated()` (general update signal)

**Implementation Notes**:
- Use QTimer for periodic cleanup of old buffer entries (every 5 minutes)
- Handle missing/invalid file data gracefully (return 0 or last known value)
- Emit signals only when values actually change to reduce QML redraws

### 6. Implement QML Frontend (main.qml)

**File**: `main.qml`

**Structure**:
```qml
import QtQuick 2.15
import QtQuick.Window 2.15
import QtCharts 2.15

Window {
    visible: true
    width: 800
    height: 480
    title: "CAN Detection Monitor"

    Column {
        anchors.fill: parent

        // Title
        Text {
            text: "CAN Bus Detection Monitor"
            font.pixelSize: 24
        }

        // Current Value Chart (single bar)
        ChartView {
            title: "Current Value"
            width: parent.width
            height: parent.height / 3

            BarSeries {
                BarSet {
                    label: "Current"
                    values: [dataCollector.currentValue]
                }
            }

            axes: [
                BarCategoryAxis { categories: ["Current"] },
                ValueAxis { min: 0; max: 15 }
            ]
        }

        // 30-Min Averages Chart (12 bars, one per 30-min period)
        ChartView {
            title: "30-Minute Averages - Last 6 Hours"
            width: parent.width
            height: parent.height / 3

            BarSeries {
                BarSet {
                    label: "Average"
                    values: dataCollector.averageValues
                }
            }

            axes: [
                BarCategoryAxis {
                    categories: ["0-30m", "30-60m", "60-90m", "90-120m",
                                "120-150m", "150-180m", "180-210m", "210-240m",
                                "240-270m", "270-300m", "300-330m", "330-360m"]
                },
                ValueAxis { min: 0; max: 15 }
            ]
        }

        // Threshold Exceedances Chart (12 bars, one per 30-min period)
        ChartView {
            title: "Threshold Exceedances (≥10) - Last 6 Hours"
            width: parent.width
            height: parent.height / 3

            BarSeries {
                BarSet {
                    label: "Count ≥10"
                    values: dataCollector.thresholdCounts
                }
            }

            axes: [
                BarCategoryAxis {
                    categories: ["0-30m", "30-60m", "60-90m", "90-120m",
                                "120-150m", "150-180m", "180-210m", "210-240m",
                                "240-270m", "270-300m", "300-330m", "330-360m"]
                },
                ValueAxis { min: 0; max: 100 }
            ]
        }
    }
}
```

**Styling**:
- Use color scheme matching LED palette where appropriate
- Font sizes suitable for display resolution
- Auto-scaling charts to fit display

### 7. Implement Init Script

**File**: `meta-can-visualizer/recipes-apps/can-visualizer/files/can-visualizer.init`

Key requirements:
- Start priority: 98 (runs after can-server at 99, before LED HAT)
- Set environment variable: `QT_QPA_PLATFORM=eglfs` for fullscreen mode
- Set `QT_QPA_EGLFS_ALWAYS_SET_MODE=1` to force display mode
- PID file: `/var/run/can-visualizer.pid`
- Use `start-stop-daemon --background --make-pidfile`
- Graceful stop with SIGTERM

**Script Structure**:
```bash
#!/bin/sh
### BEGIN INIT INFO
# Provides:          can-visualizer
# Required-Start:    $remote_fs $syslog can-server
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start CAN visualizer
# Description:       Qt6 QML CAN data visualization
### END INIT INFO

export QT_QPA_PLATFORM=eglfs
export QT_QPA_EGLFS_ALWAYS_SET_MODE=1

DAEMON=/usr/bin/can-visualizer
PIDFILE=/var/run/can-visualizer.pid
NAME=can-visualizer

# start/stop/restart functions...
```

### 8. Update build.sh Configuration

**File**: [build.sh](build.sh)

Add these sections:

**After line 81** (in variable definitions section):
```bash
CAN_VISUALIZER='IMAGE_INSTALL:append = " can-visualizer qtcharts "'
```

**After line 105** (in add_layer_if_missing calls):
```bash
add_layer_if_missing "meta-can-visualizer" "$PROJECT_ROOT/meta-can-visualizer"
```

**After line 169** (in conf append loop):
Update the `for VAR in ...` loop to include `"$CAN_VISUALIZER"`

### 9. Add qtcharts to Qt6 Package List

**File**: [build.sh](build.sh) line 164

Change:
```bash
QT_PACKAGES='IMAGE_INSTALL:append = " qtbase qtbase-tools qtdeclarative qtwayland qtsvg qttools qtdeclarative-qmlplugins "'
```

To:
```bash
QT_PACKAGES='IMAGE_INSTALL:append = " qtbase qtbase-tools qtdeclarative qtwayland qtsvg qttools qtdeclarative-qmlplugins qtcharts "'
```

### 10. Create Application Files

**main.cpp**:
- Create QGuiApplication
- Register DataCollector as QML singleton or context property
- Load main.qml with QQmlApplicationEngine
- Execute application event loop

**datacollector.h/cpp**:
- Implement all methods described in step 5
- Handle edge cases (file doesn't exist initially, invalid data, etc.)

**main.qml**:
- Implement UI described in step 6
- Connect to DataCollector properties
- Add QML Connections element to handle signals if needed

### 11. Integration and Configuration

**Dependencies to verify**:
- qtcharts available in meta-qt6 (confirmed: [qtcharts_git.bb](meta-qt6/recipes-qt/qt6/qtcharts_git.bb))
- Mesa GLES2 support (confirmed in build.sh)
- EGLFS backend enabled (confirmed in build.sh)

**Display Configuration**:
- If display doesn't auto-initialize, may need to configure in `/etc/profile` or init script
- HDMI output configuration via `RPI_EXTRA_CONFIG` in build.sh if needed

## Critical Files to Create/Modify

### New Files to Create:
1. `meta-can-visualizer/conf/layer.conf`
2. `meta-can-visualizer/recipes-apps/can-visualizer/can-visualizer_1.0.bb`
3. `meta-can-visualizer/recipes-apps/can-visualizer/files/CMakeLists.txt`
4. `meta-can-visualizer/recipes-apps/can-visualizer/files/main.cpp`
5. `meta-can-visualizer/recipes-apps/can-visualizer/files/datacollector.h`
6. `meta-can-visualizer/recipes-apps/can-visualizer/files/datacollector.cpp`
7. `meta-can-visualizer/recipes-apps/can-visualizer/files/main.qml`
8. `meta-can-visualizer/recipes-apps/can-visualizer/files/can-visualizer.init`
9. `meta-can-visualizer/recipes-apps/can-visualizer/files/LICENSE`
10. `meta-can-visualizer/README.md`

### Files to Modify:
1. [build.sh](build.sh) - Add layer, add package to IMAGE_INSTALL, add qtcharts

## Build Process

1. Run `./build.sh` to rebuild image with new layer
2. BitBake will:
   - Add meta-can-visualizer layer to bblayers.conf
   - Build Qt6 with QtCharts module
   - Compile can-visualizer C++ sources with CMake
   - Bundle QML resources
   - Install binary to /usr/bin/can-visualizer
   - Install init script to /etc/init.d/can-visualizer
   - Create rc.d symlinks for auto-start

## Verification Plan

### Build Verification:
1. Check build succeeds: `bitbake can-visualizer`
2. Check recipe installs files: `bitbake can-visualizer -c devshell` → `ls $D/usr/bin/`
3. Check image includes package: `bitbake core-image-base` → verify can-visualizer in IMAGE_INSTALL

### Runtime Verification:
1. Boot Raspberry Pi with new image
2. Check service started: `ps aux | grep can-visualizer`
3. Check PID file exists: `ls /var/run/can-visualizer.pid`
4. Check display shows Qt6 application with charts
5. Manually write values to `/var/tmp/audio_detection`: `echo 5 > /var/tmp/audio_detection`
6. Verify charts update in real-time
7. Monitor over 30+ minutes to verify averaging works
8. Write values >=10 to verify threshold counting works
9. Check logs: `tail -f /var/log/messages` (or wherever Qt logs go)

### Testing Scenarios:
1. **Current Value**: Write values 0-15, verify single bar chart updates immediately
2. **30-Min Average Calculation**: Write varying values over 30 minutes, calculate expected average for each period, verify 12 bars show correct averages
3. **Threshold Exceedances**: Over 6 hours, write mix of values <10 and >=10, verify counts per period match expected counts in 12 bars
4. **Missing File Handling**: Stop can-server, verify app doesn't crash
5. **Invalid Data**: Write non-numeric data to file, verify graceful handling
6. **Display Persistence**: Reboot RPi, verify app auto-starts with display showing all three charts (1 bar + 12 bars + 12 bars)

## Integration with Existing System

**Coexistence with LED HAT**:
- Both applications will run simultaneously
- Both monitor the same `/var/tmp/audio_detection` file
- No conflicts (file reads are non-blocking)
- LED HAT provides immediate visual feedback
- Qt6 app provides detailed historical analysis

**Service Startup Order**:
1. S40can0 (priority 40) - Initialize CAN interface
2. S99can-server (priority 99) - Start CAN receiver
3. S98can-visualizer (priority 98) - Start Qt6 visualizer **[NEW]**
4. ledhat (default priority) - Start LED HAT controller

**Resource Considerations**:
- Qt6 QML app will use GPU (VideoCore IV on RPi4)
- Memory usage: ~50-100MB for Qt6 runtime + QML
- CPU usage: Low when idle, brief spikes on data updates
- Should not interfere with CAN reception (different subsystems)

## Potential Issues and Solutions

**Issue 1: Display not initializing**
- Solution: Configure HDMI output in config.txt via RPI_EXTRA_CONFIG
- Check QT_QPA_EGLFS_* environment variables

**Issue 2: QtCharts not found during build**
- Solution: Verify qtcharts in DEPENDS and RDEPENDS
- Check qtcharts recipe is available: `bitbake qtcharts -c fetch`

**Issue 3: QML resource not embedded**
- Solution: Use qt6_add_resources in CMakeLists.txt properly
- Alternatively, install .qml files to /usr/share/can-visualizer/

**Issue 4: File watcher not triggering**
- Solution: Verify inotify kernel support
- Fallback to QTimer-based polling every 1-5 seconds

**Issue 5: Charts not updating**
- Solution: Ensure signals are emitted from DataCollector
- Check QML Connections or property bindings are correct
- Add debug qDebug() statements to verify data flow

## Implementation Status

✅ **COMPLETED** - All files have been created and integrated:

- ✅ Meta-layer structure created
- ✅ Layer configuration file
- ✅ Yocto recipe
- ✅ CMake build configuration
- ✅ C++ backend (DataCollector class)
- ✅ Qt application entry point
- ✅ QML frontend with three charts
- ✅ SysVinit script
- ✅ LICENSE file (MIT)
- ✅ README documentation
- ✅ build.sh updated with layer and packages

**Ready to build with**: `./build.sh`

## Post-Implementation Enhancements

Possible future improvements (not in scope):
- Touch interface for threshold adjustment
- Export data to CSV/log files
- Network API for remote monitoring
- Configurable chart colors and styles
- Multiple chart types (line charts, gauges, etc.)
- Alarm notifications when threshold exceeded frequently
- Integration with systemd instead of SysVinit
