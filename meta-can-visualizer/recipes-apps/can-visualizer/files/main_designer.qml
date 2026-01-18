import QtQuick 6.0
import QtQuick.Window 6.0
import QtCharts 6.0
import QtQuick.Layouts 6.0

// Designer-friendly version of main.qml
// Use this file for visual layout adjustments in Qt Creator
// Then copy layout changes back to main.qml

Window {
    id: root
    visible: true
    width: 800
    height: 480
    title: "Sound Detection Monitor - Designer Preview"
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
        spacing: 10

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
                    color: "#00b4d8"  // Blue (changes to red if ≥10)
                    borderColor: "#0096c7"
                    borderWidth: 2
                    values: [7, 0, 0]  // Static preview: current value in position 0
                }

                BarSet {
                    id: periodAvgBarSet
                    label: "Value averaged (current period)"
                    color: "#90e0ef"  // Light blue
                    borderColor: "#48cae4"
                    borderWidth: 2
                    values: [0, 6.5, 0]  // Static preview: period average in position 1
                }

                BarSet {
                    id: periodCountBarSet
                    label: "Current count of Exceedances (≥10)"
                    color: "#f72585"  // Pink/Red
                    borderColor: "#b5179e"
                    borderWidth: 2
                    values: [0, 0, 3]  // Static preview: threshold count in position 2
                }
            }
        }

        // 30-Minute Averages Chart (12 bars)
        ChartView {
            id: averagesChart
            Layout.fillWidth: true
            Layout.fillHeight: true
            title: "30-Minute Averages (Last 6 Hours)"
            titleColor: "#ffffff"
            backgroundColor: "#2a2a2a"
            legend.visible: false
            antialiasing: true

            ValueAxis {
                id: averagesYAxis
                min: 0
                max: 15
                tickCount: 6
                labelFormat: "%d"
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarCategoryAxis {
                id: averagesXAxis
                categories: ["6h", "5.5h", "5h", "4.5h", "4h", "3.5h", "3h", "2.5h", "2h", "1.5h", "1h", "0.5h"]
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarSeries {
                id: averagesSeries
                axisX: averagesXAxis
                axisY: averagesYAxis

                BarSet {
                    id: averagesBarSet
                    label: "Average"
                    color: "#90e0ef"
                    borderColor: "#48cae4"
                    borderWidth: 2
                    values: [5, 6, 7, 8, 6, 5, 7, 9, 8, 7, 6, 5]  // Static preview data
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
                max: 20
                tickCount: 5
                labelFormat: "%d"
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarCategoryAxis {
                id: thresholdXAxis
                categories: ["6h", "5.5h", "5h", "4.5h", "4h", "3.5h", "3h", "2.5h", "2h", "1.5h", "1h", "0.5h"]
                labelsColor: "#ffffff"
                gridLineColor: "#404040"
            }

            BarSeries {
                id: thresholdSeries
                axisX: thresholdXAxis
                axisY: thresholdYAxis

                BarSet {
                    id: thresholdBarSet
                    label: "Count"
                    color: "#f72585"
                    borderColor: "#b5179e"
                    borderWidth: 2
                    values: [2, 3, 1, 0, 5, 8, 12, 15, 10, 7, 4, 2]  // Static preview data
                }
            }
        }
    }
}
