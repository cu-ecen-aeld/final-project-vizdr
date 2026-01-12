# CAN Visualizer Changelog

## Version 1.2 - Color-Coded Threshold Alert (Current)

### New Features

**Visual Alert System:**
- Current value bar now automatically changes color based on threshold:
  - **BLUE** (#00b4d8) - Normal operation (value < 10)
  - **RED** (#ff4444) - ALERT! Threshold exceeded (value ≥ 10)
- Provides instant visual feedback when dangerous levels are detected

**Enhanced Bar Display:**
- Separated single BarSet into three independent BarSets for color control
- Each metric now has a distinct color:
  - **Current Value**: Blue (changes to Red when ≥10)
  - **Period Average**: Light Blue (#90e0ef)
  - **Period Count**: Pink/Red (#f72585)

### Technical Changes

**QML Changes (main.qml):**
- Replaced single `currentBarSet` with three separate BarSets:
  - `currentValueBarSet` - Current value with dynamic color
  - `periodAvgBarSet` - Period average (light blue)
  - `periodCountBarSet` - Threshold count (pink/red)

- Added color change logic in `onCurrentValueChanged()`:
  ```qml
  if (value >= 10) {
      currentValueBarSet.color = "#ff4444"  // Red alert
  } else {
      currentValueBarSet.color = "#00b4d8"  // Normal blue
  }
  ```

**Testing:**
- New test script: `test-threshold-color.sh`
- Cycles through values to demonstrate color change
- Values: 5→8→3→10(RED)→12(RED)→15(RED)→7→11(RED)→6→13(RED)→4→9

## Version 1.1 - Enhanced Current Value Chart

### New Features

Added two additional columns to the current value chart to display real-time 30-minute period statistics:

1. **Current Value** - The most recent detection value (existing feature)
2. **Period Average** - Running average of all values received during the current 30-minute period
3. **Period ≥10 Count** - Number of threshold exceedances (value ≥ 10) in the current 30-minute period

### Technical Changes

#### C++ Backend (datacollector.h/cpp)

**New Properties:**
- `currentPeriodAverage` (double) - Real-time average for current 30-minute window
- `currentPeriodThresholdCount` (int) - Real-time count of exceedances in current period

**New Signals:**
- `currentPeriodAverageChanged()` - Emitted when period average updates
- `currentPeriodThresholdCountChanged()` - Emitted when period count updates

**Modified Functions:**
- `calculateStatistics()` - Now calculates current period (index 0) separately and emits individual signals for real-time updates

#### QML UI (main.qml)

**Current Value Chart Updates:**
- Changed from 1 bar to 3 bars
- Updated title: "Current Values & 30-Min Period Statistics"
- Updated X-axis categories: ["Current", "Period Avg", "Period ≥10"]
- Updated Y-axis max from 15 to 20 to accommodate threshold counts
- Enabled legend display
- Updated BarSet values from `[0]` to `[0, 0, 0]`

**New Signal Handlers:**
- `onCurrentPeriodAverageChanged()` - Updates second bar with period average
- `onCurrentPeriodThresholdCountChanged()` - Updates third bar with period count

### Data Flow

```
New value arrives in /var/tmp/audio_detection
    ↓
DataCollector::readDataFromFile()
    ↓
DataCollector::addDataPoint(value)
    ↓
DataCollector::calculateStatistics()
    ↓
Calculate current period stats (index 0 = last 30 minutes)
    ↓
Emit currentPeriodAverageChanged()
Emit currentPeriodThresholdCountChanged()
    ↓
QML Connections update bars:
  - Bar 0: currentValue (instant)
  - Bar 1: currentPeriodAverage (rolling average)
  - Bar 2: currentPeriodThresholdCount (exceedances in period)
```

### Visual Changes

**Before:**
- Single bar showing only current value

**After:**
- Three bars side-by-side:
  1. Blue bar - Current detection value
  2. Blue bar - Average over current 30-min period
  3. Blue bar - Count of exceedances (≥10) in current period

### Example Scenario

If during the current 30-minute period:
- Current value: 7
- Values received: [5, 8, 7, 12, 6, 11, 7, 9]
- Average: (5+8+7+12+6+11+7+9)/8 = 8.125
- Threshold exceedances: 2 (values 12 and 11 are ≥10)

The chart displays:
- **Bar 1 (Current):** 7
- **Bar 2 (Period Avg):** 8.13
- **Bar 3 (Period ≥10):** 2

### Files Modified

- `datacollector.h` - Added 2 properties, 2 signals, 2 member variables
- `datacollector.cpp` - Updated constructor, added 2 getters, modified calculateStatistics()
- `main.qml` - Updated current chart from 1 to 3 bars, added 2 signal handlers
- `main_designer.qml` - Updated preview with sample data

### Compatibility

- Backward compatible with existing Yocto build
- No changes required to recipe files
- No changes to data file format or other components

### Testing

To test locally:
```bash
cd build/Qt6.7.2-Debug
./can-visualizer
```

Or use the preview tool:
```bash
./preview-layout.sh  # Shows static sample data
```

To test with changing values:
```bash
# In one terminal
./run-qt6.7.2.sh

# In another terminal
echo 5 > /var/tmp/audio_detection
sleep 2
echo 12 > /var/tmp/audio_detection
sleep 2
echo 8 > /var/tmp/audio_detection
# Watch the three bars update in real-time
```
