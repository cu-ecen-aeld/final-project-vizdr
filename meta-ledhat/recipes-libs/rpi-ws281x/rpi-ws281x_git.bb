
SUMMARY = "Raspberry Pi WS281x LED library"
DESCRIPTION = "Library for controlling WS281x LEDs on Raspberry Pi using C/C++."
HOMEPAGE = "https://github.com/jgarff/rpi_ws281x"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"
SRCREV = "7fc0bf8b31d715bbecf28e852ede5aaa388180da"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# Make sure headers are installed
do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/ws2811.h ${D}${includedir}/
    install -m 0644 ${S}/rpihw.h ${D}${includedir}/
    install -m 0644 ${S}/pwm.h ${D}${includedir}/
}

# Package the development files
FILES:${PN}-dev += "${includedir}/*.h"

# Allow empty main package if needed
ALLOW_EMPTY:${PN} = "1"

# Optional: ensure headers are installed to rpi_ws281x subdir
# do_install_append() {
#     # If you want to install headers in a subdir, uncomment below:
#     # install -d ${D}${includedir}/rpi_ws281x
#     # install -m 0644 ${S}/ws2811.h ${D}${includedir}/rpi_ws281x/
    
#     # By default, CMake install puts ws2811.h in ${includedir}
# }

