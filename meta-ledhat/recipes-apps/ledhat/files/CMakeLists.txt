cmake_minimum_required(VERSION 3.10)
project(ledhat C)

# Find the ws2811 library
find_library(WS2811_LIB ws2811 REQUIRED)

# Find the ws2811 header
find_path(WS2811_INCLUDE_DIR ws2811.h)

# Create the executable
add_executable(ledhat main.c)

# Link the library m - standard math library
target_link_libraries(ledhat ${WS2811_LIB} m)

# Include directories if found
if(WS2811_INCLUDE_DIR)
    target_include_directories(ledhat PRIVATE ${WS2811_INCLUDE_DIR})
endif()

# Install the binary
install(TARGETS ledhat DESTINATION bin)
