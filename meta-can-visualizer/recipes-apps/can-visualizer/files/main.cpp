#include <QApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QDebug>

#include "datacollector.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);

    app.setApplicationName("CAN Visualizer");
    app.setApplicationVersion("1.0");
    app.setOrganizationName("AESD Final Project");

    // Create data collector instance
    DataCollector dataCollector;

    // Create QML engine
    QQmlApplicationEngine engine;

    // Expose DataCollector to QML as context property
    engine.rootContext()->setContextProperty("dataCollector", &dataCollector);

    // Load main QML file
    const QUrl url(QStringLiteral("qrc:/main.qml"));

    QObject::connect(&engine, &QQmlApplicationEngine::objectCreated,
                     &app, [url](QObject *obj, const QUrl &objUrl) {
        if (!obj && url == objUrl) {
            qCritical() << "Failed to load QML file:" << url;
            QCoreApplication::exit(-1);
        }
    }, Qt::QueuedConnection);

    engine.load(url);

    if (engine.rootObjects().isEmpty()) {
        qCritical() << "No root objects created from QML";
        return -1;
    }

    qDebug() << "CAN Visualizer started successfully";

    return app.exec();
}
