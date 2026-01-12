#!/bin/bash

# Test script to demonstrate threshold color change
# The current value bar changes to RED when value >= 10

echo "CAN Visualizer - Threshold Color Change Test"
echo "=============================================="
echo ""
echo "This script will write values to /var/tmp/audio_detection"
echo "Watch the first bar (Current) change color:"
echo "  - BLUE when value < 10"
echo "  - RED when value >= 10"
echo ""
echo "Make sure can-visualizer is running!"
echo ""
read -p "Press Enter to start the test..."

# Array of test values
values=(5 8 3 10 12 15 7 11 6 13 4 9)

echo ""
echo "Starting test sequence..."
echo ""

for value in "${values[@]}"; do
    echo "$value" > /var/tmp/audio_detection

    if [ "$value" -ge 10 ]; then
        echo "[$value] - Should be RED (threshold exceeded)"
    else
        echo "[$value] - Should be BLUE (below threshold)"
    fi

    sleep 2
done

echo ""
echo "Test complete!"
echo ""
echo "Summary:"
echo "  Values < 10:  Blue bar"
echo "  Values >= 10: Red bar (ALERT!)"
