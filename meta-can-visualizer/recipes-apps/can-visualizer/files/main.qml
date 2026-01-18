import QtQuick 6.0
import QtQuick.Window 6.0
import QtCharts 6.0
import QtQuick.Layouts 6.0

Window {
    id: root
    visible: true
    width: 800
    height: 480
    title: "Sound Detection Monitor"
    color: "#1a1a1a"

    // Handle Ctrl+C keyboard shortcut to quit application
    Shortcut {
        sequence: "Ctrl+C"
        onActivated: {
            console.log("Ctrl+C pressed - exiting application")
            Qt.quit()
        }
    }

    ColumnLayout {
        anchors.fill: parent
        anchors.margins: 10
        spacing: 5

        // Title
        Text {
            Layout.fillWidth: true
            Layout.preferredHeight: 40
            text: "Sound Detection Monitor over CAN Bus"
            font.pixelSize: 28
            font.bold: true
            color: "#ffffff"
            horizontalAlignment: Text.AlignHCenter
            verticalAlignment: Text.AlignVCenter
        }

        // Current Value Chart (3 bars: current, period average, period threshold count)
        ChartView {
            id: currentChart
            Layout.fillWidth: true
            Layout.fillHeight: true
            title: "Current Values & 30-Min Period Statistics"
            titleColor: "#ffffff"
            backgroundColor: "#2a2a2a"
            legend.visible: true
            legend.color: "#ffffff"
            legend.borderColor: "#ffffff"
            legend.labelColor: "#ffffff"
            legend.alignment: Qt.AlignBottom
            antialiasing: true

            ValueAxis {
                id: currentYAxis
                min: 0
                max: 20
                tickCount: 5
                labelFormat: "%d"
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarCategoryAxis {
                id: currentXAxis
                categories: ["Current value", "Value Averaged", "Current Exceedances (Value ≥10)"]
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarSeries {
                id: currentSeries
                axisX: currentXAxis
                axisY: currentYAxis
                barWidth: 0.8  // Make bars slightly narrower for better spacing

                // Separate BarSets with values spread across all positions for equal spacing
                BarSet {
                    id: currentValueBarSet
                    label: "Current value"
                    color: "#00b4d8"  // Blue (will change to red if ≥10)
                    borderColor: "#0096c7"
                    borderWidth: 2
                    values: [0, 0, 0]  // [Current, Value averaged (current period), Period Exceedances Count]
                }

                BarSet {
                    id: periodAvgBarSet
                    label: "Value averaged (current period)"
                    color: "#90e0ef"  // Light blue
                    borderColor: "#48cae4"
                    borderWidth: 2
                    values: [0, 0, 0]  // [Current, Value averaged (current period), Period Exceedances Count]
                }

                BarSet {
                    id: periodCountBarSet
                    label: "Current count of Exceedances (≥10)"
                    color: "#f72585"  // Pink/Red
                    borderColor: "#b5179e"
                    borderWidth: 2
                    values: [0, 0, 0]  // [Current, Value averaged (current period), Period Exceedances Count]
                }
            }
        }

        // 30-Minute Averages Chart (12 bars)
        ChartView {
            id: averagesChart
            Layout.fillWidth: true
            Layout.fillHeight: true
            title: "30-Minute Averages - Last 6 Hours"
            titleColor: "#ffffff"
            backgroundColor: "#2a2a2a"
            legend.visible: false
            antialiasing: true

            ValueAxis {
                id: averagesYAxis
                min: 0
                max: 15
                tickCount: 6
                labelFormat: "%.1f"
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarCategoryAxis {
                id: averagesXAxis
                categories: [
                    "0-30m", "30-60m", "60-90m", "90-120m",
                    "120-150m", "150-180m", "180-210m", "210-240m",
                    "240-270m", "270-300m", "300-330m", "330-360m"
                ]
                labelsColor: "#ffffff"
                labelsAngle: -45
                gridLineColor: "#404040"
            }

            BarSeries {
                id: averagesSeries
                axisX: averagesXAxis
                axisY: averagesYAxis

                BarSet {
                    id: averagesBarSet
                    label: "Average"
                    color: "#48cae4"
                    borderColor: "#00b4d8"
                    borderWidth: 2
                    values: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                }
            }
        }

        // Threshold Exceedances Chart (12 bars)
        ChartView {
            id: thresholdChart
            Layout.fillWidth: true
            Layout.fillHeight: true
            title: "Threshold Exceedances (≥10) - Last 6 Hours"
            titleColor: "#ffffff"
            backgroundColor: "#2a2a2a"
            legend.visible: false
            antialiasing: true

            ValueAxis {
                id: thresholdYAxis
                min: 0
                max: 100
                tickCount: 6
                labelFormat: "%d"
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarCategoryAxis {
                id: thresholdXAxis
                categories: [
                    "0-30m", "30-60m", "60-90m", "90-120m",
                    "120-150m", "150-180m", "180-210m", "210-240m",
                    "240-270m", "270-300m", "300-330m", "330-360m"
                ]
                labelsColor: "#ffffff"
                labelsAngle: -45
                gridLineColor: "#404040"
            }

            BarSeries {
                id: thresholdSeries
                axisX: thresholdXAxis
                axisY: thresholdYAxis

                BarSet {
                    id: thresholdBarSet
                    label: "Count ≥10"
                    color: "#ff6b6b"
                    borderColor: "#ee5a6f"
                    borderWidth: 2
                    values: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                }
            }
        }
    }

    // Connections to update charts when data changes
    Connections {
        target: dataCollector

        function onCurrentValueChanged() {
            var value = dataCollector.currentValue

            // Update current value in position 0 (first bar)
            currentValueBarSet.replace(0, value)

            // Change color to red if threshold exceeded (≥10)
            if (value >= 10) {
                currentValueBarSet.color = "#ff4444"  // Red
                currentValueBarSet.borderColor = "#cc0000"
            } else {
                currentValueBarSet.color = "#00b4d8"  // Blue
                currentValueBarSet.borderColor = "#0096c7"
            }
        }

        function onCurrentPeriodAverageChanged() {
            // Update period average in position 1 (second bar)
            periodAvgBarSet.replace(1, dataCollector.currentPeriodAverage)
        }

        function onCurrentPeriodThresholdCountChanged() {
            // Update period threshold count in position 2 (third bar)
            periodCountBarSet.replace(2, dataCollector.currentPeriodThresholdCount)
        }

        function onAverageValuesChanged() {
            var avgList = dataCollector.averageValues
            for (var i = 0; i < avgList.length && i < 12; i++) {
                averagesBarSet.replace(i, avgList[i])
            }
        }

        function onThresholdCountsChanged() {
            var countList = dataCollector.thresholdCounts
            for (var i = 0; i < countList.length && i < 12; i++) {
                thresholdBarSet.replace(i, countList[i])
            }
        }
    }

    Component.onCompleted: {
        console.log("CAN Visualizer QML loaded successfully")
    }
}
