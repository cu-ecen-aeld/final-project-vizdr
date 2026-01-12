# CAN Visualizer Application Files

## Production Files (Required for Yocto Build)

These files are used by the Yocto build system and deployed to the Raspberry Pi:

- **CMakeLists.txt** - CMake build configuration
- **main.cpp** - Application entry point (uses QApplication for QtCharts support)
- **main.qml** - QML user interface with three charts
- **datacollector.h** - C++ backend class header
- **datacollector.cpp** - C++ backend implementation (monitors /var/tmp/audio_detection)
- **can-visualizer.init** - SysVinit startup script (runs at boot with priority 98)
- **LICENSE** - MIT license

## Development Tools (Optional - Not Included in Yocto Build)

### Layout Designer Tool

For visually editing the QML layout:

- **designer-preview/** - Complete preview tool with QtWidgets support
  - **CMakeLists.txt** - Build config for preview tool
  - **preview_designer.cpp** - Preview application
  - **main_designer.qml** - Copy of main_designer.qml for preview
  - **build/** - Build directory (generated)

- **main_designer.qml** - Designer-friendly QML with static sample data
- **edit-layout.sh** - Launch Qt Creator + live preview
- **preview-layout.sh** - Run preview only (no editor)
- **LAYOUT_EDITING.md** - Complete guide for layout editing

**Usage:**
```bash
./edit-layout.sh  # Opens Qt Creator and shows live preview
```

See [LAYOUT_EDITING.md](LAYOUT_EDITING.md) for detailed instructions.

### Local Testing Helper

- **run-qt6.7.2.sh** - Run application with Qt 6.7.2 libraries

**Usage:**
```bash
./run-qt6.7.2.sh  # Runs the application locally with correct Qt version
```

## File Structure Summary

```
files/
├── Production (Yocto build):
│   ├── CMakeLists.txt
│   ├── main.cpp
│   ├── main.qml
│   ├── datacollector.h
│   ├── datacollector.cpp
│   ├── can-visualizer.init
│   └── LICENSE
│
├── Development tools:
│   ├── designer-preview/         (Layout designer tool)
│   ├── main_designer.qml         (Designer version of main.qml)
│   ├── edit-layout.sh            (Launch designer)
│   ├── preview-layout.sh         (Preview only)
│   ├── run-qt6.7.2.sh            (Local testing)
│   └── LAYOUT_EDITING.md         (Documentation)
│
└── build/                        (Generated - not tracked)
    ├── Qt6.7.2-Debug/
    └── designer/
```

## What Gets Included in the Yocto Image

Only the production files are included via the recipe:

```bitbake
SRC_URI = "file://CMakeLists.txt \
           file://main.cpp \
           file://datacollector.h \
           file://datacollector.cpp \
           file://main.qml \
           file://can-visualizer.init \
           file://LICENSE"
```

The development tools (`designer-preview/`, `*.sh`, `main_designer.qml`, etc.) are **not** included in the Yocto build and only exist for local development convenience.

## Key Technical Details

### Why QApplication instead of QGuiApplication?

`main.cpp` uses `QApplication` (not `QGuiApplication`) because QtCharts internally uses QtWidgets components for text rendering (chart titles, axis labels). Using `QGuiApplication` causes segmentation faults.

### Why Qt 6.7.2 Locally?

The local development environment uses Qt 6.7.2 to match the Yocto build version, avoiding ABI incompatibilities.

### Data Flow

```
/var/tmp/audio_detection (written by Socket_CAN_Receiver)
    ↓
DataCollector (QFileSystemWatcher monitors file)
    ↓
QML Context Property (dataCollector)
    ↓
main.qml (binds to dataCollector signals)
    ↓
Charts update (BarSet.replace() on signal)
```

## Building Locally

```bash
cd build/Qt6.7.2-Debug
cmake ../.. -DCMAKE_PREFIX_PATH=~/Qt/6.7.2/gcc_64
make -j$(nproc)
./can-visualizer
```

## Building with Yocto

```bash
./build.sh
```

The `can-visualizer` application will be included in the image and start automatically at boot.
