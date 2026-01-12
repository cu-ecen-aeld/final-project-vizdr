SUMMARY = "Qt6 QML CAN Data Visualization Application"
DESCRIPTION = "Visualizes CAN bus detection data with charts showing current value, 30-minute averages, and threshold exceedances over 6 hours"
HOMEPAGE = "https://github.com/vizdr/final-project-assignment-app-vizdr"
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

DEPENDS = "qtbase qtdeclarative qtdeclarative-native qtcharts"

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
