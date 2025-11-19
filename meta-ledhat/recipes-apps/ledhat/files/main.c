/*
 * Simple WS2812B demo using rpi_ws281x library
 * Build and run as root or via systemd service
 */

/* #include <stdio.h>
#include <stdint.h>
#include <unistd.h>
#include "ws2811.h"

#include <signal.h>

static volatile sig_atomic_t g_stop = 0;

static void handle_sig(int sig)
{
    (void)sig;
    g_stop = 1;
}

#define LED_COUNT 64
#define GPIO_PIN 18
#define DMA_CHANNEL 10

// Colors in GRB format
#define COLOR_NAVY 0x00000080
#define COLOR_PURPLE 0x00800080
#define COLOR_BLUE 0x000000FF
#define COLOR_GREEN 0x0000FF00
#define COLOR_BROWN 0x00800000
#define COLOR_MAGENTA 0x00FF00FF
#define COLOR_PINK 0x00FFC0CB
#define COLOR_ORANGE 0x00FFA500
#define COLOR_YELLOW 0x00FFFF00
#define COLOR_RED 0x00FF0000

#define COLOR_OFF 0x00000000
#define COLOR_WHITE 0x00FFFFFF

#define COLOR_CYAN 0x0000FFFF
#define COLOR_LIME 0x0000FF00
#define COLOR_TEAL 0x00008080

#define READFILETPATH "/var/tmp/audio_detection"
#define READ_INTERVAL_SEC 5 // Read file every 5 seconds

static int *colors = (int[]){
    COLOR_OFF,
    COLOR_NAVY,
    COLOR_PURPLE,
    COLOR_BLUE,
    COLOR_GREEN,
    COLOR_BROWN,
    COLOR_MAGENTA,
    COLOR_PINK,
    COLOR_ORANGE,
    COLOR_YELLOW,
    COLOR_RED,
};

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
*/
// int main(int argc, char **argv)
// {
//     struct sigaction sa;

//     /* Setup signal handling */
//     memset(&sa, 0, sizeof(sa));
//     sa.sa_handler = handle_sig;
//     sigaction(SIGINT, &sa, NULL);
//     sigaction(SIGTERM, &sa, NULL);

//     FILE *readFile;

//     if (ws2811_init(&ledstring) != WS2811_SUCCESS)
//     {
//         fprintf(stderr, "ws2811_init failed\n");
//         return -1;
//     }

//     // --- Main loop ---
//     while (!g_stop)
//     {
//         readFile = fopen(READFILETPATH, "r");
//         if (readFile == NULL)
//         {
//             fprintf(stderr, "Failed to open file for reading: %s\n", READFILETPATH);
//             sleep(READ_INTERVAL_SEC);
//             continue;
//         }
//         else
//         {
//             char buffer[16];
//             if (fgets(buffer, sizeof(buffer), readFile) != NULL)
//             {
//                 int colorIndex = atoi(buffer);
//                 if (colorIndex < 0 || colorIndex >= sizeof(colors) / sizeof(colors[0]))
//                 {
//                     colorIndex = 0; // Default to off if out of range
//                 }
//                 // Set all LEDs to the selected color
//                 for (int i = 0; i < LED_COUNT; i++)
//                 {
//                     ledstring.channel[0].leds[i] = colors[colorIndex];
//                 }
//                 ws2811_render(&ledstring);
//             }
//             fclose(readFile);
//             sleep(READ_INTERVAL_SEC);
//         }
//         // simple chase pattern
//         for (int iter = 0; iter < 5; iter++)
//         {
//             for (int i = 0; i < LED_COUNT; i++)
//             {
//                 // set all to off
//                 for (int j = 0; j < LED_COUNT; j++)
//                 {
//                     ledstring.channel[0].leds[j] = 0x00000000;
//                 }
//                 // one pixel red
//                 ledstring.channel[0].leds[i] = 0x00FF0000; // GRB order: green=0x00, red=0xFF
//                 ws2811_render(&ledstring);
//                 usleep(50000);
//             }
//         }
//     }
//     // clear
//     for (int i = 0; i < LED_COUNT; i++)
//     {
//         ledstring.channel[0].leds[i] = 0x00000000;
//     }
//     ws2811_render(&ledstring);
//     ws2811_fini(&ledstring);
//     return 0;
// }

/*
 * led_controller  (with INOTIFY real-time file monitoring)
 *
 * Reads color index from INPUT_FILE and drives WS2811 LEDs.
 * - Normal: apply color immediately when changed.
 * - Heartbeat: toggle OFF/ON if unchanged.
 * - Error mode:
 *      * parse error → WHITE flicker
 * - Alarm mode:
 *      * out-of-range index greater than COLORS_COUNT → RED flicker
 * - Auto-recovery using INOTIFY (no more stat() polling!)
 * - Logs to /var/log/led-controller.log
 *
 * Build Makefile example:
 *   gcc -O2 -Wall -o led_controller led_controller.c -lws2811
 */

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <limits.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <time.h>
#include <signal.h>
#include <sys/stat.h>
#include <sys/inotify.h>
#include <fcntl.h>

#include "ws2811.h" /* rpi_ws281x */

/* ----------------------------------------------- */
/* Config */
/* ----------------------------------------------- */
#define INPUT_FILE "/var/tmp/audio_detection"
#define LOG_FILE "/var/log/led-controller.log"

#define READ_INTERVAL_SEC 5
#define FLICKER_INTERVAL_US 500000

#define LED_COUNT 64
#define TARGET_FREQ WS2811_TARGET_FREQ
#define GPIO_PIN 18
#define DMA 10
#define STRIP_TYPE WS2811_STRIP_GRB

/* Colors in GRB format */
#define COLOR_NAVY 0x00000080
#define COLOR_PURPLE 0x00800080
#define COLOR_BLUE 0x000000FF
#define COLOR_GREEN 0x0000FF00
#define COLOR_BROWN 0x00800000
#define COLOR_MAGENTA 0x00FF00FF
#define COLOR_PINK 0x00FFC0CB
#define COLOR_ORANGE 0x00FFA500
#define COLOR_YELLOW 0x00FFFF00
#define COLOR_RED 0x00FF0000

#define COLOR_OFF 0x00000000
#define COLOR_WHITE 0x00FFFFFF

/* Color palette */
static const ws2811_led_t colors[] = {
    COLOR_NAVY,
    COLOR_PURPLE,
    COLOR_BLUE,
    COLOR_GREEN,
    COLOR_BROWN,
    COLOR_MAGENTA,
    COLOR_PINK,
    COLOR_ORANGE,
    COLOR_YELLOW,
    COLOR_RED,
};
#define COLORS_COUNT (sizeof(colors) / sizeof(colors[0]))

/* ----------------------------------------------- */
static volatile sig_atomic_t g_stop = 0;

/* ws2811 structure */
static ws2811_t ledstring = {
    .freq = TARGET_FREQ,
    .dmanum = DMA,
    .channel = {
        [0] = {
            .gpionum = GPIO_PIN,
            .count = LED_COUNT,
            .invert = 0,
            .brightness = 255,
            .strip_type = STRIP_TYPE,
        },
        [1] = {0}},
};

/* ----------------------------------------------- */
/* Helpers */
/* ----------------------------------------------- */

static void handle_sig(int sig)
{
    (void)sig;
    g_stop = 1;
}
/*--------------------------------------------------------*/
/* Logging */
static FILE *open_log(void)
{
    FILE *f = fopen(LOG_FILE, "a");
    if (!f)
    {
        perror("log");
        return stdout;
    }
    return f;
}

static void log_ts(FILE *f)
{
    time_t now = time(NULL);
    struct tm t;
    localtime_r(&now, &t);
    fprintf(f, "%04d-%02d-%02d %02d:%02d:%02d ",
            t.tm_year + 1900, t.tm_mon + 1, t.tm_mday,
            t.tm_hour, t.tm_min, t.tm_sec);
}
/* ----------------------------------------------- */
/* LED control */
static void set_all(ws2811_led_t c)
{
    for (int i = 0; i < ledstring.channel[0].count; i++)
        ledstring.channel[0].leds[i] = c;
    ws2811_render(&ledstring);
}

/*
 * Read integer index from file.
 *
 * Returns:
 *   1 = success (out_index set)
 *   0 = parse error
 *  -1 = open/stat failure
 */
/* ----------------------------------------------- */
static int read_index(const char *path, long *out_index, FILE *logf)
{
    FILE *fp = fopen(path, "r");
    if (!fp)
    {
        log_ts(logf);
        fprintf(logf, "Cannot open %s: %s\n", path, strerror(errno));
        fflush(logf);
        return -1;
    }

    char buf[64];
    if (!fgets(buf, sizeof(buf), fp))
    {
        fclose(fp);
        log_ts(logf);
        fprintf(logf, "Empty file %s\n", path);
        fflush(logf);
        return 0;
    }
    fclose(fp);

    errno = 0;
    char *end = NULL;
    long val = strtol(buf, &end, 10);
    if (end == buf || errno)
    {
        log_ts(logf);
        fprintf(logf, "Parse error in %s: '%s'\n", path, buf);
        fflush(logf);
        return 0;
    }

    *out_index = val;
    return 1;
}

/* ----------------------------------------------- */
/* INOTIFY: wait for next change event */
/* ----------------------------------------------- */
static int wait_for_file_change(int in_fd)
{
    char buf[4096];
    while (!g_stop)
    {
        int len = read(in_fd, buf, sizeof(buf));
        if (len <= 0)
            return 0;

        /* There may be multiple events */
        const struct inotify_event *ev;
        for (char *p = buf; p < buf + len;
             p += sizeof(struct inotify_event) + ev->len)
        {
            ev = (const struct inotify_event *)p;

            if (ev->mask & (IN_CLOSE_WRITE | IN_MOVED_TO))
            {
                return 1; /* change detected */
            }
        }
    }
    return 0;
}

/* ----------------------------------------------- */
/* MAIN */
/* ----------------------------------------------- */
int main(int argc, char **argv)
{
    FILE *logf = open_log();
    log_ts(logf);
    fprintf(logf, "LED controller starting (with inotify)\n");
    fflush(logf);

    signal(SIGINT, handle_sig);
    signal(SIGTERM, handle_sig);

    /* Init LEDs */
    if (ws2811_init(&ledstring) != WS2811_SUCCESS)
    {
        log_ts(logf);
        fprintf(logf, "ws2811_init failed\n");
        fflush(logf);
        return 1;
    }

    // -------------------------------------------------------------
    // Ensure the input file exists; if not, create it with "0"
    // -------------------------------------------------------------
    struct stat st;
    if (stat(INPUT_FILE, &st) == -1)
    {
        if (errno == ENOENT)
        {
            fprintf(stderr, "[INIT] INPUT_FILE not found. Creating %s with value 0...\n", INPUT_FILE);

            int fd = open(INPUT_FILE, O_WRONLY | O_CREAT | O_TRUNC, 0644);
            if (fd < 0)
            {
                perror("open (creating INPUT_FILE)");
                exit(EXIT_FAILURE);
            }

            const char *init_value = "0\n";
            if (write(fd, init_value, strlen(init_value)) != (ssize_t)strlen(init_value))
            {
                perror("write (initializing INPUT_FILE)");
                close(fd);
                exit(EXIT_FAILURE);
            }

            close(fd);
            fprintf(stderr, "[INIT] INPUT_FILE created successfully.\n");
        }
        else
        {
            perror("stat INPUT_FILE");
            exit(EXIT_FAILURE);
        }
    }

    /* ----------------------------------------------- */
    /* INOTIFY setup */
    int in_fd_notify = inotify_init1(IN_NONBLOCK);
    if (in_fd_notify < 0)
    {
        log_ts(logf);
        fprintf(logf, "inotify_init failed: %s\n", strerror(errno));
        fflush(logf);
        return 1;
    }

    int wd = inotify_add_watch(in_fd_notify, 
                               INPUT_FILE, IN_MODIFY | IN_CLOSE_WRITE);
    if (wd < 0)
    {
        log_ts(logf);
        fprintf(logf, "inotify_add_watch failed: %s\n", strerror(errno));
        fflush(logf);
        return 1;
    }
    /* ----------------------------------------------- */
    /* Animation state */
    long last_index = LONG_MIN;
    ws2811_led_t last_color = COLOR_OFF;
    int heartbeat = 0;

    ws2811_led_t alarm_red = COLOR_RED;
    ws2811_led_t white = COLOR_WHITE;
    /* ----------------------------------------------- */
    /* ------------------------------ */
    /* Main loop                      */
    /* ------------------------------ */
    while (!g_stop)
    {
        long idx;
        int r = read_index(INPUT_FILE, &idx, logf);

        int parse_err = (r == 0);
        int out_of_range = (r == 1 && (idx < 0 || idx >= (long)COLORS_COUNT));
        int ok = (r == 1 && !out_of_range);

        /* ----------------------------------------------- */
        /* ERROR AND ALARM MODE (flicker, with inotify recovery) */
        /* ----------------------------------------------- */
        if (parse_err || out_of_range)
        {
            int is_red = idx >= (long)COLORS_COUNT;

            log_ts(logf);
            fprintf(logf, "Entering %s flicker mode\n",
                    is_red ? "RED" : "WHITE");
            fflush(logf);

            while (!g_stop)
            {
                /* ON */
                set_all(is_red ? alarm_red : white);

                /* wait half period OR file change */
                for (int i = 0; i < 10 && !g_stop; i++)
                {
                    usleep(FLICKER_INTERVAL_US / 10);
                    /* immediate file change? */
                    if (wait_for_file_change(in_fd_notify))
                        goto try_recover;
                }

                /* OFF */
                set_all(COLOR_OFF);

                for (int i = 0; i < 10 && !g_stop; i++)
                {
                    usleep(FLICKER_INTERVAL_US / 10);
                    if (wait_for_file_change(in_fd_notify))
                        goto try_recover;
                }
            }
            break;

        try_recover:;
            long new_idx;
            int rr = read_index(INPUT_FILE, &new_idx, logf);
            if (rr == 1 && new_idx >= 0 && new_idx < (long)COLORS_COUNT)
            {
                log_ts(logf);
                fprintf(logf, "Recovered with index %ld\n", new_idx);
                fflush(logf);
                last_index = new_idx;
                last_color = colors[new_idx];
                set_all(last_color);
                continue;
            }
            else
            {
                /* stay in flicker mode */
                continue;
            }
        }

        /* ------------------------------ */
        /* NORMAL MODE                    */
        /* ------------------------------ */
        if (ok && idx != last_index)
        {
            last_index = idx;
            last_color = colors[idx];

            log_ts(logf);
            fprintf(logf, "Color changed -> %ld\n", idx);
            fflush(logf);

            set_all(last_color);
        }
        else
        {
            /* Heartbeat */
            heartbeat = !heartbeat;
            set_all(heartbeat ? COLOR_OFF : last_color);

            log_ts(logf);
            fprintf(logf, "Heartbeat (%s)\n", heartbeat ? "OFF" : "ON");
            fflush(logf);

            /* Wait up to READ_INTERVAL_SEC for inotify change */
            int loops = READ_INTERVAL_SEC * 10;
            while (loops-- > 0 && !g_stop)
            {
                usleep(100000); /* 0.1s */
                if (wait_for_file_change(in_fd))
                    break;
            }
        }
    }

    log_ts(logf);
    fprintf(logf, "Shutting down\n");
    fflush(logf);

    set_all(COLOR_OFF);
    ws2811_fini(&ledstring);

    if (logf != stdout)
        fclose(logf);
    return 0;
}
