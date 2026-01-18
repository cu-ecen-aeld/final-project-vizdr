#include <QApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QDebug>
#include <csignal>

#include "datacollector.h"

// Global pointer to application for signal handler
static QApplication* g_app = nullptr;

// Signal handler for SIGINT (Ctrl+C) and SIGTERM
void signalHandler(int signal)
{
    if (signal == SIGINT) {
        qDebug() << "\nReceived SIGINT (Ctrl+C), shutting down gracefully...";
    } else if (signal == SIGTERM) {
        qDebug() << "\nReceived SIGTERM, shutting down gracefully...";
    }

    if (g_app) {
        g_app->quit();
    }
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    g_app = &app;

    // Install signal handlers for graceful shutdown
    std::signal(SIGINT, signalHandler);   // Ctrl+C
    std::signal(SIGTERM, signalHandler);  // kill command

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
    qDebug() << "Press Ctrl+C to exit";

    int result = app.exec();

    qDebug() << "CAN Visualizer shutdown complete";
    return result;
}
