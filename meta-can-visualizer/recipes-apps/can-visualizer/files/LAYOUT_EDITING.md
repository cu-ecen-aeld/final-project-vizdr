# Layout Editing Guide

## Why Design Mode is Inactive in Qt Creator

Qt Creator's visual QML Designer doesn't support:
- QtCharts module components
- QML files with context properties (like `dataCollector`)
- Resource-based QML loading (`qrc:/`)

## Solution: Use the Designer Preview Tool

We've created a special preview tool that lets you visually edit the layout.

## Quick Start

```bash
./edit-layout.sh
```

This will:
1. Open `main_designer.qml` in Qt Creator
2. Launch a live preview window with sample data
3. Let you edit and test the layout visually

## Workflow

### Step 1: Launch the Editor
```bash
cd /media/vladimir/SanDisk256/Assignment-Final-Project-AELD/meta-can-visualizer/recipes-apps/can-visualizer/files
./edit-layout.sh
```

### Step 2: Edit main_designer.qml
- Use Qt Creator's code editor (Edit mode)
- Modify layout properties (see examples below)
- Save the file (Ctrl+S)

### Step 3: Preview Changes
- Close the preview window (Ctrl+C)
- Run `./edit-layout.sh` again to see your changes

### Step 4: Apply to Production
When satisfied with the layout:
```bash
# Copy layout changes from main_designer.qml to main.qml
# Then rebuild the application
cd build/Qt6.7.2-Debug
make
```

## Common Layout Adjustments

### Change Window Size
```qml
Window {
    width: 1024   // Make wider (default: 800)
    height: 768   // Make taller (default: 480)
}
```

### Make All Charts Equal Height
```qml
ChartView {
    id: currentChart
    Layout.fillWidth: true
    Layout.preferredHeight: 160  // Fixed height (remove fillHeight)
    // ... rest of properties
}

// Apply same preferredHeight to all three charts
```

### Add Spacing Between Charts
```qml
ColumnLayout {
    anchors.fill: parent
    anchors.margins: 10  // Padding around edges
    spacing: 20          // Space between charts (default: 10)
    // ...
}
```

### Change Chart Background Colors
```qml
ChartView {
    backgroundColor: "#1a1a1a"  // Darker background
    titleColor: "#00ff00"       // Green title
    // ...
}
```

### Adjust Bar Colors
```qml
BarSet {
    color: "#ff6b6b"        // Red bars
    borderColor: "#c92a2a"  // Darker red border
    borderWidth: 3          // Thicker border
}
```

### Change Axis Ranges
```qml
ValueAxis {
    min: 0
    max: 20        // Higher max value (default: 15)
    tickCount: 5   // Number of tick marks
}
```

### Adjust Chart Titles
```qml
ChartView {
    title: "Real-Time Detection Value"  // Custom title
    titleFont.pixelSize: 18             // Larger title
    titleFont.bold: true                // Bold title
}
```

### Add Chart Legends
```qml
ChartView {
    legend.visible: true              // Show legend (default: false)
    legend.alignment: Qt.AlignBottom  // Position at bottom
}
```

### Responsive Height Ratios
Give different charts different amounts of space:
```qml
// Current value gets 30% of height
ChartView {
    id: currentChart
    Layout.fillWidth: true
    Layout.preferredHeight: 150
}

// Averages gets 35% of height
ChartView {
    id: averagesChart
    Layout.fillWidth: true
    Layout.preferredHeight: 180
}

// Threshold gets 35% of height
ChartView {
    id: thresholdChart
    Layout.fillWidth: true
    Layout.preferredHeight: 180
}
```

## Files Explained

- **main.qml** - Production QML file (loads real data from dataCollector)
- **main_designer.qml** - Designer preview file (loads static sample data)
- **preview_designer.cpp** - C++ application that runs the preview
- **edit-layout.sh** - Script to launch Qt Creator + preview
- **preview-layout.sh** - Script to only run preview (no editor)

## Troubleshooting

### Preview crashes with segfault
- Make sure you're using Qt 6.7.2: `~/Qt/6.7.2/gcc_64/bin/qmake --version`
- The preview tool uses QApplication (not QGuiApplication) to support QtCharts

### Changes don't appear
- Make sure you saved main_designer.qml (Ctrl+S)
- Restart the preview: close it and run `./edit-layout.sh` again
- Check for QML syntax errors in Qt Creator's "Issues" panel

### Qt Creator shows errors
- Red underlines in Qt Creator are often false positives for QtCharts
- If the preview runs without errors, you can ignore Qt Creator warnings

## Alternative: Direct Editing with Live App

For quick tweaks:

1. Run the real application:
   ```bash
   ./run-qt6.7.2.sh &
   ```

2. Edit main.qml in any text editor

3. Stop and restart the application to see changes:
   ```bash
   killall can-visualizer
   ./run-qt6.7.2.sh &
   ```

## Qt Documentation

- [QtCharts QML Types](https://doc.qt.io/qt-6/qtcharts-qmlmodule.html)
- [ColumnLayout](https://doc.qt.io/qt-6/qml-qtquick-layouts-columnlayout.html)
- [ChartView](https://doc.qt.io/qt-6/qml-qtcharts-chartview.html)
- [BarSeries](https://doc.qt.io/qt-6/qml-qtcharts-barseries.html)
