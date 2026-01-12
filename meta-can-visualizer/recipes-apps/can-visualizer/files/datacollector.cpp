#include "datacollector.h"
#include <QFile>
#include <QTextStream>
#include <QDebug>

DataCollector::DataCollector(QObject *parent)
    : QObject(parent)
    , m_fileWatcher(new QFileSystemWatcher(this))
    , m_cleanupTimer(new QTimer(this))
    , m_currentValue(0)
    , m_currentPeriodAverage(0.0)
    , m_currentPeriodThresholdCount(0)
{
    // Initialize average values and threshold counts lists with zeros
    for (int i = 0; i < NUM_PERIODS; ++i) {
        m_averageValues.append(0.0);
        m_thresholdCounts.append(0);
    }

    // Setup file watcher
    m_fileWatcher->addPath(DATA_FILE);
    connect(m_fileWatcher, &QFileSystemWatcher::fileChanged,
            this, &DataCollector::onFileChanged);

    // Setup cleanup timer (run every 5 minutes)
    m_cleanupTimer->setInterval(5 * 60 * 1000);  // 5 minutes in milliseconds
    connect(m_cleanupTimer, &QTimer::timeout,
            this, &DataCollector::cleanupOldData);
    m_cleanupTimer->start();

    // Delay initial read to ensure QML is fully loaded
    QTimer::singleShot(100, this, &DataCollector::readDataFromFile);
}

DataCollector::~DataCollector()
{
}

int DataCollector::currentValue() const
{
    return m_currentValue;
}

double DataCollector::currentPeriodAverage() const
{
    return m_currentPeriodAverage;
}

int DataCollector::currentPeriodThresholdCount() const
{
    return m_currentPeriodThresholdCount;
}

QVariantList DataCollector::averageValues() const
{
    return m_averageValues;
}

QVariantList DataCollector::thresholdCounts() const
{
    return m_thresholdCounts;
}

void DataCollector::onFileChanged(const QString &path)
{
    Q_UNUSED(path);

    // Re-add the path to the watcher (it gets removed after file modification on some systems)
    if (!m_fileWatcher->files().contains(DATA_FILE)) {
        m_fileWatcher->addPath(DATA_FILE);
    }

    readDataFromFile();
}

void DataCollector::readDataFromFile()
{
    QFile file(DATA_FILE);

    if (!file.exists()) {
        qWarning() << "Data file does not exist:" << DATA_FILE;
        return;
    }

    if (!file.open(QIODevice::ReadOnly | QIODevice::Text)) {
        qWarning() << "Failed to open data file:" << DATA_FILE;
        return;
    }

    QTextStream in(&file);
    QString line = in.readLine().trimmed();
    file.close();

    if (line.isEmpty()) {
        qWarning() << "Data file is empty";
        return;
    }

    bool ok = false;
    int value = line.toInt(&ok);

    if (!ok) {
        qWarning() << "Failed to parse value from file:" << line;
        return;
    }

    // Add new data point
    addDataPoint(value);

    // Update current value
    int oldCurrentValue = m_currentValue;
    m_currentValue = value;

    if (oldCurrentValue != m_currentValue) {
        emit currentValueChanged();
    }

    // Recalculate statistics
    calculateStatistics();

    emit dataUpdated();  // Not used currently but available for future use
}

void DataCollector::addDataPoint(int value)
{
    DataPoint point;
    point.value = value;
    point.timestamp = QDateTime::currentDateTime();

    m_dataBuffer.append(point);

    qDebug() << "Added data point: value =" << value
             << ", timestamp =" << point.timestamp.toString(Qt::ISODate);
}

void DataCollector::calculateStatistics()
{
    QVariantList newAverages;
    QVariantList newCounts;

    // Calculate current period (index 0 = most recent 30 minutes)
    double currentAvg = calculatePeriodAverage(0);
    int currentCount = calculatePeriodThresholdCount(0);

    // Update current period values
    bool currentAvgChanged = qAbs(m_currentPeriodAverage - currentAvg) > 0.01;
    bool currentCountChanged = (m_currentPeriodThresholdCount != currentCount);

    m_currentPeriodAverage = currentAvg;
    m_currentPeriodThresholdCount = currentCount;

    if (currentAvgChanged) {
        emit currentPeriodAverageChanged();
    }
    if (currentCountChanged) {
        emit currentPeriodThresholdCountChanged();
    }

    // Calculate all periods for historical charts
    for (int i = 0; i < NUM_PERIODS; ++i) {
        double avg = calculatePeriodAverage(i);
        int count = calculatePeriodThresholdCount(i);

        newAverages.append(avg);
        newCounts.append(count);
    }

    // Update and emit signals if changed
    bool averagesChanged = (newAverages != m_averageValues);
    bool countsChanged = (newCounts != m_thresholdCounts);

    if (averagesChanged) {
        m_averageValues = newAverages;
        emit averageValuesChanged();
    }

    if (countsChanged) {
        m_thresholdCounts = newCounts;
        emit thresholdCountsChanged();
    }
}

double DataCollector::calculatePeriodAverage(int periodIndex) const
{
    QPair<QDateTime, QDateTime> timeRange = getPeriodTimeRange(periodIndex);
    QDateTime startTime = timeRange.first;
    QDateTime endTime = timeRange.second;

    double sum = 0.0;
    int count = 0;

    for (const DataPoint &point : m_dataBuffer) {
        if (point.timestamp >= startTime && point.timestamp < endTime) {
            sum += point.value;
            count++;
        }
    }

    if (count == 0) {
        return 0.0;
    }

    return sum / count;
}

int DataCollector::calculatePeriodThresholdCount(int periodIndex) const
{
    QPair<QDateTime, QDateTime> timeRange = getPeriodTimeRange(periodIndex);
    QDateTime startTime = timeRange.first;
    QDateTime endTime = timeRange.second;

    int count = 0;

    for (const DataPoint &point : m_dataBuffer) {
        if (point.timestamp >= startTime && point.timestamp < endTime) {
            if (point.value >= THRESHOLD_VALUE) {
                count++;
            }
        }
    }

    return count;
}

QPair<QDateTime, QDateTime> DataCollector::getPeriodTimeRange(int periodIndex) const
{
    QDateTime now = QDateTime::currentDateTime();

    // Period 0 = 0-30 minutes ago
    // Period 1 = 30-60 minutes ago
    // ...
    // Period 11 = 330-360 minutes ago

    int endMinutesAgo = periodIndex * PERIOD_MINUTES;
    int startMinutesAgo = (periodIndex + 1) * PERIOD_MINUTES;

    QDateTime endTime = now.addSecs(-endMinutesAgo * 60);
    QDateTime startTime = now.addSecs(-startMinutesAgo * 60);

    return qMakePair(startTime, endTime);
}

void DataCollector::cleanupOldData()
{
    QDateTime now = QDateTime::currentDateTime();
    QDateTime cutoffTime = now.addSecs(-6 * 60 * 60);  // 6 hours ago

    // Remove data points older than 6 hours
    m_dataBuffer.erase(
        std::remove_if(m_dataBuffer.begin(), m_dataBuffer.end(),
                       [cutoffTime](const DataPoint &point) {
                           return point.timestamp < cutoffTime;
                       }),
        m_dataBuffer.end()
    );

    qDebug() << "Cleaned up old data. Buffer size:" << m_dataBuffer.size();
}
