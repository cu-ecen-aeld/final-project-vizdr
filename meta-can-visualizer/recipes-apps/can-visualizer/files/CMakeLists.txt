cmake_minimum_required(VERSION 3.16)

project(can-visualizer VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

# Find Qt6 packages
find_package(Qt6 REQUIRED COMPONENTS Core Gui Qml Quick Widgets Charts)

# Configure QML file as a resource
set(QML_FILE "${CMAKE_CURRENT_SOURCE_DIR}/main.qml")
set(QML_RESOURCES
    "<RCC><qresource prefix=\"/\"><file alias=\"main.qml\">${QML_FILE}</file></qresource></RCC>"
)
file(WRITE "${CMAKE_CURRENT_BINARY_DIR}/qml.qrc" ${QML_RESOURCES})
qt6_add_resources(QML_RES "${CMAKE_CURRENT_BINARY_DIR}/qml.qrc")

# Add executable
add_executable(can-visualizer
    main.cpp
    datacollector.h
    datacollector.cpp
    ${QML_RES}
)

# Link Qt6 libraries
target_link_libraries(can-visualizer
    Qt6::Core
    Qt6::Gui
    Qt6::Qml
    Qt6::Quick
    Qt6::Widgets
    Qt6::Charts
)

# Install binary
include(GNUInstallDirs)
install(TARGETS can-visualizer
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
