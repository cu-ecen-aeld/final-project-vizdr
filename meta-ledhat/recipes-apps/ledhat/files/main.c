/*
* Simple WS2812B demo using rpi_ws281x library
* Build and run as root or via systemd service
*/


#include <stdio.h>
#include <stdint.h>
#include <unistd.h>
#include "ws2811.h"

#define LED_COUNT 64
#define GPIO_PIN 18
#define DMA_CHANNEL 10

ws2811_t ledstring = {
.freq = WS2811_TARGET_FREQ,
.dmanum = DMA_CHANNEL,
.channel = {
[0] = {
.gpionum = GPIO_PIN,
.count = LED_COUNT,
.invert = 0,
.brightness = 255,
.strip_type = WS2811_STRIP_GRB,
},
[1] = {
.gpionum = 0,
.count = 0,
.invert = 0,
.brightness = 0,
},
},
};

int main(int argc, char **argv) {
if (ws2811_init(&ledstring) != WS2811_SUCCESS) {
fprintf(stderr, "ws2811_init failed\n");
return -1;
}

// simple chase pattern
for (int iter = 0; iter < 5; iter++) {
for (int i = 0; i < LED_COUNT; i++) {
// set all to off
for (int j = 0; j < LED_COUNT; j++) {
ledstring.channel[0].leds[j] = 0x00000000;
}
// one pixel red
ledstring.channel[0].leds[i] = 0x00FF0000; // GRB order: green=0x00, red=0xFF
ws2811_render(&ledstring);
usleep(50000);
}
}

// clear
for (int i = 0; i < LED_COUNT; i++) {
ledstring.channel[0].leds[i] = 0x00000000;
}
ws2811_render(&ledstring);
ws2811_fini(&ledstring);
return 0;
}