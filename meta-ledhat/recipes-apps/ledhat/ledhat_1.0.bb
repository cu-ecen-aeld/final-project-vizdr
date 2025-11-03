SUMMARY = "LED demo application using rpi-ws281x"
DESCRIPTION = "Simple demo application to control WS281x LEDs on Raspberry Pi."
HOMEPAGE = "https://github.com/jgarff/rpi_ws281x"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b273abc358f1fedf17f19d5af2180293"



SRC_URI = "file://CMakeLists.txt \
           file://main.c \
           file://ledhat.init \
           file://LICENSE"

S = "${WORKDIR}"

DEPENDS = "rpi-ws281x"

inherit cmake pkgconfig

PV = "1.0"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/ledhat ${D}${bindir}/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/ledhat.init ${D}${sysconfdir}/init.d/
}