#ifndef DATACOLLECTOR_H
#define DATACOLLECTOR_H

#include <QObject>
#include <QVariantList>
#include <QDateTime>
#include <QFileSystemWatcher>
#include <QTimer>
#include <QVector>

/**
 * @brief Data point structure to store value and timestamp
 */
struct DataPoint {
    int value;
    QDateTime timestamp;
};

/**
 * @brief DataCollector class monitors CAN detection data file and calculates statistics
 *
 * Monitors /var/tmp/audio_detection file for changes and maintains a time-series buffer
 * of detection values. Calculates:
 * - Current value (latest reading)
 * - 30-minute averages for each of 12 periods over last 6 hours
 * - Threshold exceedance counts (>=10) for each of 12 periods over last 6 hours
 */
class DataCollector : public QObject
{
    Q_OBJECT
    Q_PROPERTY(int currentValue READ currentValue NOTIFY currentValueChanged)
    Q_PROPERTY(double currentPeriodAverage READ currentPeriodAverage NOTIFY currentPeriodAverageChanged)
    Q_PROPERTY(int currentPeriodThresholdCount READ currentPeriodThresholdCount NOTIFY currentPeriodThresholdCountChanged)
    Q_PROPERTY(QVariantList averageValues READ averageValues NOTIFY averageValuesChanged)
    Q_PROPERTY(QVariantList thresholdCounts READ thresholdCounts NOTIFY thresholdCountsChanged)

public:
    explicit DataCollector(QObject *parent = nullptr);
    ~DataCollector();

    // Property getters
    int currentValue() const;
    double currentPeriodAverage() const;
    int currentPeriodThresholdCount() const;
    QVariantList averageValues() const;
    QVariantList thresholdCounts() const;

signals:
    void currentValueChanged();
    void currentPeriodAverageChanged();
    void currentPeriodThresholdCountChanged();
    void averageValuesChanged();
    void thresholdCountsChanged();
    void dataUpdated();

private slots:
    void onFileChanged(const QString &path);
    void cleanupOldData();

private:
    void readDataFromFile();
    void calculateStatistics();
    void addDataPoint(int value);

    // Calculate average for a specific 30-minute period
    double calculatePeriodAverage(int periodIndex) const;

    // Count threshold exceedances (>=10) for a specific 30-minute period
    int calculatePeriodThresholdCount(int periodIndex) const;

    // Get time range for a period (0 = 0-30min ago, 1 = 30-60min ago, etc.)
    QPair<QDateTime, QDateTime> getPeriodTimeRange(int periodIndex) const;

private:
    static constexpr const char* DATA_FILE = "/var/tmp/audio_detection";
    static constexpr int THRESHOLD_VALUE = 10;
    static constexpr int NUM_PERIODS = 12;  // 12 x 30-minute periods = 6 hours
    static constexpr int PERIOD_MINUTES = 30;

    QFileSystemWatcher *m_fileWatcher;
    QTimer *m_cleanupTimer;

    QVector<DataPoint> m_dataBuffer;
    int m_currentValue;
    double m_currentPeriodAverage;
    int m_currentPeriodThresholdCount;
    QVariantList m_averageValues;
    QVariantList m_thresholdCounts;
};

#endif // DATACOLLECTOR_H
