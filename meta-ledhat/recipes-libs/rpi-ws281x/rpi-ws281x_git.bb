# SUMMARY = "WS281x LED library for Raspberry Pi (rpi_ws281x)"
# LICENSE = "MIT"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=42a5cbffebf76d92b8a8cc6d9d6e0a2f"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https"
# SRCREV = "${AUTOREV}"

# PV = "0.1+git${SRCPV}"
# S = "${WORKDIR}/git"

# inherit pkgconfig

# # Build using the provided Makefile
# do_compile() {
# oe_runmake CFLAGS="${CFLAGS} -fPIC"
# }

# do_install() {
# install -d ${D}${libdir}
# install -m 0644 libws2811.a ${D}${libdir}/
# install -d ${D}${includedir}
# install -m 0644 *.h ${D}${includedir}/
# }

# FILES_${PN} = "${libdir}/*.a ${includedir}/*.h"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=4725d8c48e196561ab5d5b0e6e04f4bc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# DEPENDS = "virtual/kernel"

# EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} -I${S}' 'LDFLAGS=${LDFLAGS}'"

# do_compile() {
#     # Build the library
#     oe_runmake -C ${S}
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
    
#     # Install library
#     install -m 0755 ${S}/*.a ${D}${libdir}/ || true
#     install -m 0755 ${S}/*.so* ${D}${libdir}/ || true
    
#     # Install headers
#     install -m 0644 ${S}/*.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/*"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# do_compile() {
#     cd ${S}
#     # Compile library sources
#     ${CC} ${CFLAGS} -fPIC -c *.c
#     ${AR} rcs libws2811.a *.o
#     ${CC} ${LDFLAGS} -shared -o libws2811.so *.o
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
    
#     # Install libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/
    
#     # Install headers
#     install -m 0644 ${S}/ws2811.h ${D}${includedir}/
#     install -m 0644 ${S}/rpihw.h ${D}${includedir}/
#     install -m 0644 ${S}/pwm.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/libws2811.so"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# DEPENDS = "scons-native"

# export CROSS_COMPILE = "${TARGET_PREFIX}"

# do_compile() {
#     cd ${S}
#     # Run SCons with proper cross-compilation settings
#     ${STAGING_BINDIR_NATIVE}/scons
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
    
#     # Install static library
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
    
#     # Install headers
#     install -m 0644 ${S}/ws2811.h ${D}${includedir}/
#     install -m 0644 ${S}/rpihw.h ${D}${includedir}/
#     install -m 0644 ${S}/pwm.h ${D}${includedir}/
    
#     # Install version.h if it exists
#     if [ -f ${S}/version.h ]; then
#         install -m 0644 ${S}/version.h ${D}${includedir}/
#     fi
# }

# FILES:${PN}-staticdev = "${libdir}/*.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# do_compile() {
#     cd ${S}
    
#     # Generate version.h
#     cat > version.h << EOF
# #ifndef __VERSION_H__
# #define __VERSION_H__
# #define VERSION "1.0.0"
# #endif
# EOF
    
#     # Compile library sources
#     ${CC} ${CFLAGS} -fPIC -c ws2811.c mailbox.c pwm.c pcm.c dma.c rpihw.c
    
#     # Create static library
#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
    
#     # Create shared library
#     ${CC} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
    
#     # Build test program from main.c
#     ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L. -lws2811 -lm -lrt -lpthread
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
#     install -d ${D}${bindir}
    
#     # Install libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/
    
#     # Install test program
#     install -m 0755 ${S}/ledhat-test ${D}${bindir}/ledhat-test
    
#     # Install headers
#     install -m 0644 ${S}/ws2811.h ${D}${includedir}/
#     install -m 0644 ${S}/rpihw.h ${D}${includedir}/
#     install -m 0644 ${S}/pwm.h ${D}${includedir}/
#     install -m 0644 ${S}/clk.h ${D}${includedir}/
#     install -m 0644 ${S}/dma.h ${D}${includedir}/
#     install -m 0644 ${S}/gpio.h ${D}${includedir}/
#     install -m 0644 ${S}/mailbox.h ${D}${includedir}/
#     install -m 0644 ${S}/pcm.h ${D}${includedir}/
#     install -m 0644 ${S}/version.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# # Inherit base to get proper toolchain setup
# inherit pkgconfig

# # Ensure toolchain dependencies
# DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}compilerlibs virtual/libc"

# do_compile[depends] += "virtual/${TARGET_PREFIX}gcc:do_populate_sysroot"

# do_compile() {
#     # Generate version.h
#     cat > ${S}/version.h << EOF
# #ifndef __VERSION_H__
# #define __VERSION_H__
# #define VERSION "1.0.0"
# #endif
# EOF
    
#     # Compile library sources
#     cd ${S}

#     bbnote "CC is: ${CC}"
#     bbnote "PATH is: ${PATH}"

#     ${CC} ${CFLAGS} -fPIC -c ws2811.c
#     ${CC} ${CFLAGS} -fPIC -c mailbox.c
#     ${CC} ${CFLAGS} -fPIC -c pwm.c
#     ${CC} ${CFLAGS} -fPIC -c pcm.c
#     ${CC} ${CFLAGS} -fPIC -c dma.c
#     ${CC} ${CFLAGS} -fPIC -c rpihw.c
    
#     # Create static library
#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
    
#     # Create shared library
#     ${CC} ${CFLAGS} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
    
#     # Build test program from main.c
#     ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L${S} -lws2811 -lm -lrt -lpthread
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
#     install -d ${D}${bindir}
    
#     # Install libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/
    
#     # Install test program
#     install -m 0755 ${S}/ledhat-test ${D}${bindir}/ledhat-test
    
#     # Install headers
#     install -m 0644 ${S}/ws2811.h ${D}${includedir}/
#     install -m 0644 ${S}/rpihw.h ${D}${includedir}/
#     install -m 0644 ${S}/pwm.h ${D}${includedir}/
#     install -m 0644 ${S}/clk.h ${D}${includedir}/
#     install -m 0644 ${S}/dma.h ${D}${includedir}/
#     install -m 0644 ${S}/gpio.h ${D}${includedir}/
#     install -m 0644 ${S}/mailbox.h ${D}${includedir}/
#     install -m 0644 ${S}/pcm.h ${D}${includedir}/
#     install -m 0644 ${S}/version.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# # Inherit base to get proper toolchain setup
# inherit pkgconfig

# do_compile() {
#     # Generate version.h
#     cat > ${S}/version.h << 'EOF'
# #ifndef __VERSION_H__
# #define __VERSION_H__
# #define VERSION "1.0.0"
# #endif
# EOF
    
#     # Compile library sources
#     cd ${S}
    
#     ${CC} ${CFLAGS} -fPIC -c ws2811.c -o ws2811.o
#     ${CC} ${CFLAGS} -fPIC -c mailbox.c -o mailbox.o
#     ${CC} ${CFLAGS} -fPIC -c pwm.c -o pwm.o
#     ${CC} ${CFLAGS} -fPIC -c pcm.c -o pcm.o
#     ${CC} ${CFLAGS} -fPIC -c dma.c -o dma.o
#     ${CC} ${CFLAGS} -fPIC -c rpihw.c -o rpihw.o
    
#     # Create static library
#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
    
#     # Create shared library
#     ${CC} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
    
#     # Build test program from main.c
#     ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L${S} -lws2811 -lm -lrt -lpthread
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
#     install -d ${D}${bindir}
    
#     # Install libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/
    
#     # Install test program
#     install -m 0755 ${S}/ledhat-test ${D}${bindir}/ledhat-test
    
#     # Install headers
#     install -m 0644 ${S}/ws2811.h ${D}${includedir}/
#     install -m 0644 ${S}/rpihw.h ${D}${includedir}/
#     install -m 0644 ${S}/pwm.h ${D}${includedir}/
#     install -m 0644 ${S}/clk.h ${D}${includedir}/
#     install -m 0644 ${S}/dma.h ${D}${includedir}/
#     install -m 0644 ${S}/gpio.h ${D}${includedir}/
#     install -m 0644 ${S}/mailbox.h ${D}${includedir}/
#     install -m 0644 ${S}/pcm.h ${D}${includedir}/
#     install -m 0644 ${S}/version.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"
# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# inherit pkgconfig autotools

# do_compile() {
#     mkdir -p ${S}
#     # Generate version.h
#     cat > ${S}/version.h << 'EOF'
# #ifndef __VERSION_H__
# #define __VERSION_H__
# #define VERSION "1.0.0"
# #endif
# EOF

#     cd ${S}
#     bbnote "Compiler: ${CC}"
#     bbnote "Compiler path: $(which ${CC})"

#     ${CC} ${CFLAGS} -fPIC -c ws2811.c -o ws2811.o
#     ${CC} ${CFLAGS} -fPIC -c mailbox.c -o mailbox.o
#     ${CC} ${CFLAGS} -fPIC -c pwm.c -o pwm.o
#     ${CC} ${CFLAGS} -fPIC -c pcm.c -o pcm.o
#     ${CC} ${CFLAGS} -fPIC -c dma.c -o dma.o
#     ${CC} ${CFLAGS} -fPIC -c rpihw.c -o rpihw.o

#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
#     ${CC} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o

#     ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L${S} -lws2811 -lm -lrt -lpthread
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
#     install -d ${D}${bindir}

#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/
#     install -m 0755 ${S}/ledhat-test ${D}${bindir}/ledhat-test

#     install -m 0644 ${S}/*.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs (e.g. WS2812B, SK6812)"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# # Source from official GitHub repository
# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"
# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# S = "${WORKDIR}/git"

# # This library can be built directly with the toolchain; no autotools required
# inherit pkgconfig autotools

# # Ensure proper dependencies for cross-compilation
# DEPENDS += "virtual/${TARGET_PREFIX}gcc virtual/libc"

# # Add debug messages and ensure directory creation
# do_compile() {
#     bbnote "==========================================================="
#     bbnote "Building rpi_ws281x for ${TARGET_ARCH}"
#     bbnote "Source directory: ${S}"
#     bbnote "Compiler: ${CC}"
#     bbnote "PATH: ${PATH}"
#     bbnote "==========================================================="

#     # Ensure source directory exists (fix for 'Directory nonexistent')
#     mkdir -p ${S}

#     # Generate version.h (required by library)
#     cat > ${S}/version.h << 'EOF'
# #ifndef __VERSION_H__
# #define __VERSION_H__
# #define VERSION "1.0.0"
# #endif
# EOF

#     cd ${S}

#     # Compile the library objects
#     ${CC} ${CFLAGS} -fPIC -c ws2811.c -o ws2811.o
#     ${CC} ${CFLAGS} -fPIC -c mailbox.c -o mailbox.o
#     ${CC} ${CFLAGS} -fPIC -c pwm.c -o pwm.o
#     ${CC} ${CFLAGS} -fPIC -c pcm.c -o pcm.o
#     ${CC} ${CFLAGS} -fPIC -c dma.c -o dma.o
#     ${CC} ${CFLAGS} -fPIC -c rpihw.c -o rpihw.o

#     # Create static and shared libraries
#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
#     ${CC} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o

#     # Optional: Build demo program (if main.c exists)
#     if [ -f main.c ]; then
#         bbnote "Compiling demo test: ledhat-test"
#         ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L${S} -lws2811 -lm -lrt -lpthread
#     fi
# }

# do_install() {
#     bbnote "Installing rpi-ws281x binaries and headers"

#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
#     install -d ${D}${bindir}

#     # Install libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/

#     # Install test binary if built
#     if [ -f ${S}/ledhat-test ]; then
#         install -m 0755 ${S}/ledhat-test ${D}${bindir}/
#     fi

#     # Install header files
#     for header in ${S}/*.h; do
#         [ -f "$header" ] && install -m 0644 "$header" ${D}${includedir}/
#     done
# }

# # Package output
# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Library for driving WS281X LEDs (NeoPixels) from Raspberry Pi using DMA and PWM."
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"
# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# # Source directory (auto-adjusted below)
# S = "${WORKDIR}/git"

# inherit pkgconfig autotools

# # ------------------------------------------------------------------------
# # Automatic path detection and debugging output
# # ------------------------------------------------------------------------
# python do_adjust_s() {
#     import os
#     base = d.getVar("S")
#     files = os.listdir(base)
#     found = False
#     if "ws2811.c" not in files:
#         subdirs = [f for f in files if os.path.isdir(os.path.join(base, f))]
#         for sub in subdirs:
#             if os.path.exists(os.path.join(base, sub, "ws2811.c")):
#                 new_s = os.path.join(base, sub)
#                 d.setVar("S", new_s)
#                 bb.note(f"Adjusted S to {new_s}")
#                 found = True
#                 break
#     if not found:
#         bb.note(f"Using S={base}")
# }
# addtask do_adjust_s before do_configure after do_unpack

# # ------------------------------------------------------------------------

# do_compile() {
#     bbnote "==========================================================="
#     bbnote "Building rpi_ws281x for ${TARGET_ARCH}"
#     bbnote "Source directory: ${S}"
#     bbnote "Compiler: ${CC}"
#     bbnote "==========================================================="

#     if [ ! -f ${S}/ws2811.c ]; then
#         bbfatal "ws2811.c not found in ${S}. Check S variable."
#     fi

#     # Generate version.h
#     cat > ${S}/version.h << 'EOF'
# #ifndef __VERSION_H__
# #define __VERSION_H__
# #define VERSION "1.0.0"
# #endif
# EOF

#     cd ${S}

#     # Compile all sources
#     ${CC} ${CFLAGS} -fPIC -c ws2811.c -o ws2811.o
#     ${CC} ${CFLAGS} -fPIC -c mailbox.c -o mailbox.o
#     ${CC} ${CFLAGS} -fPIC -c pwm.c -o pwm.o
#     ${CC} ${CFLAGS} -fPIC -c pcm.c -o pcm.o
#     ${CC} ${CFLAGS} -fPIC -c dma.c -o dma.o
#     ${CC} ${CFLAGS} -fPIC -c rpihw.c -o rpihw.o

#     # Create static and shared libraries
#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o
#     ${CC} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o

#     # Optional test binary (main.c must exist)
#     if [ -f main.c ]; then
#         ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L${S} -lws2811 -lm -lrt -lpthread
#     else
#         bbwarn "main.c not found — skipping test binary"
#     fi
# }

# do_install() {
#     install -d ${D}${libdir}
#     install -d ${D}${includedir}
#     install -d ${D}${bindir}

#     # Install libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/

#     # Install test binary if built
#     if [ -f ${S}/ledhat-test ]; then
#         install -m 0755 ${S}/ledhat-test ${D}${bindir}/ledhat-test
#     fi

#     # Install headers
#     install -m 0644 ${S}/*.h ${D}${includedir}/
# }

# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# COMPATIBLE_MACHINE = "^rpi$"
# SUMMARY = "Userspace Raspberry Pi PWM library for WS281X LEDs"
# DESCRIPTION = "Userspace Raspberry Pi library for controlling WS281X LEDs"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# PV = "0.1+git${SRCPV}"
# SRCREV = "${AUTOREV}"

# # The initial source directory
# S = "${WORKDIR}/git"

# # Inherit autotools for compatibility, pkgconfig if needed
# inherit pkgconfig autotools

# # Ensure toolchain dependencies for cross-compilation
# DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}compilerlibs virtual/libc"

# # -------------------------------------------------------------------
# # Adjust ${S} dynamically if ws2811.c is in a subdirectory
# python do_adjust_s() {
#     import os
#     base = d.getVar("S")
#     files = os.listdir(base)
#     found = False
#     if "ws2811.c" not in files:
#         subdirs = [f for f in files if os.path.isdir(os.path.join(base, f))]
#         for sub in subdirs:
#             if os.path.exists(os.path.join(base, sub, "ws2811.c")):
#                 new_s = os.path.join(base, sub)
#                 d.setVar("S", new_s)
#                 bb.note(f"Adjusted S to {new_s}")
#                 found = True
#                 break
#     if not found:
#         if "ws2811.c" in files:
#             bb.note(f"Using S={base}")
#         else:
#             bb.fatal(f"ws2811.c not found in {base} or its subdirectories")
# }
# addtask do_adjust_s before do_configure after do_unpack

# # -------------------------------------------------------------------
# do_compile() {
#     cd ${S}

#     bb.note("Compiling rpi_ws281x library")


#     # Compile library sources
#     ${CC} ${CFLAGS} -fPIC -c ws2811.c -o ws2811.o
#     ${CC} ${CFLAGS} -fPIC -c mailbox.c -o mailbox.o
#     ${CC} ${CFLAGS} -fPIC -c pwm.c -o pwm.o
#     ${CC} ${CFLAGS} -fPIC -c pcm.c -o pcm.o
#     ${CC} ${CFLAGS} -fPIC -c dma.c -o dma.o
#     ${CC} ${CFLAGS} -fPIC -c rpihw.c -o rpihw.o

#     # Create static library
#     ${AR} rcs libws2811.a ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o

#     # Create shared library
#     ${CC} ${LDFLAGS} -shared -o libws2811.so ws2811.o mailbox.o pwm.o pcm.o dma.o rpihw.o

#     # Build test program
#     ${CC} ${CFLAGS} ${LDFLAGS} -o ledhat-test main.c -L${S} -lws2811 -lm -lrt -lpthread
# }

# # -------------------------------------------------------------------
# do_install() {
#     # Create directories
#     install -d ${D}${libdir} ${D}${includedir} ${D}${bindir}

#     # Install compiled libraries
#     install -m 0644 ${S}/libws2811.a ${D}${libdir}/
#     install -m 0755 ${S}/libws2811.so ${D}${libdir}/

#     # Install test binary
#     install -m 0755 ${S}/ledhat-test ${D}${bindir}/ledhat-test

#     # Install headers
#     install -m 0644 ${S}/*.h ${D}${includedir}/
# }

# # -------------------------------------------------------------------
# # Package file locations
# FILES:${PN} = "${libdir}/libws2811.so ${bindir}/ledhat-test"
# FILES:${PN}-staticdev = "${libdir}/libws2811.a"
# FILES:${PN}-dev = "${includedir}/*"

# # Only for Raspberry Pi targets
# COMPATIBLE_MACHINE = "^rpi"

# SUMMARY = "Raspberry Pi WS281x LED library"
# DESCRIPTION = "Library for controlling WS281x LEDs on Raspberry Pi using C/C++."
# HOMEPAGE = "https://github.com/jgarff/rpi_ws281x"
# LICENSE = "BSD-2-Clause"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=9dcf340793a1d73c5211edc8238767dc"

# # Git source
# SRC_URI = "git://github.com/jgarff/rpi_ws281x.git;protocol=https;branch=master"

# SRCREV = "7fc0bf8b31d715bbecf28e852ede5aaa388180da"

# PV = "1.0+git${SRCPV}"

# # Use CMake but override the missing install step
# inherit cmake pkgconfig

# # Optional: specify build directory
# S = "${WORKDIR}/git"

# # No upstream install target, so we do it manually
# do_install() {
#     # Install static library
#     install -d ${D}${libdir}
#     install -m 0755 ${B}/libws2811.a ${D}${libdir}/
#     # Install headers (the library headers are all in the source tree)
#     # install -d ${D}${includedir}/rpi_ws281x
#     # install -m 0644 ${S}/*.h ${D}${includedir}/rpi_ws281x/

#     install -d ${D}${includedir}
#     install -m 0644 ${S}/ws2811.h ${D}${includedir}/
# }

# # Package contents (Yocto needs this to know what files to include)
# FILES:${PN} += "${libdir}/*.a ${includedir}/rpi_ws281x"

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

