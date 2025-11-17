#!/bin/bash
# Script to build image for raspberry pi 4 using Yocto Project
# Author: Siddhant Jajoo, Vladimir Zdravkov.

PROJECT_ROOT="$(pwd)"

set -e

############################################################
# Function to add layer if not already added

add_layer_if_missing() {
    LAYER_NAME=$1
    LAYER_PATH=$2

    # Convert to absolute path
    LAYER_ABS_PATH=$(readlink -f "$LAYER_PATH")
    
    if [ ! -d "$LAYER_ABS_PATH" ]; then
        echo "Error: Layer path $LAYER_ABS_PATH not found!"
        exit 1
    fi

    if ! bitbake-layers show-layers | grep -q "$LAYER_ABS_PATH"; then
        echo "Adding $LAYER_NAME layer..."
        bitbake-layers add-layer "$LAYER_ABS_PATH"
    else
        echo "$LAYER_NAME layer already added."
    fi
}

############################################################
# Clean build directories if 'clean' argument is provided

if [ "$1" = "clean" ]; then
    echo "Cleaning Yocto build directories..."
    rm -rf tmp sstate-cache downloads cache
    echo "Cleanup complete."
    exit 0
fi

############################################################
# Manual recipe cleanup: ./build.sh <clean-type> <recipe>
# clean       → bitbake -c clean <recipe>
# cleanall    → bitbake -c cleanall <recipe>
# cleansstate → bitbake -c cleansstate <recipe>
############################################################

if [[ "$1" =~ ^(clean|cleanall|cleansstate)$ ]] && [ -n "$2" ]; then
    CLEAN_ACTION="$1"
    RECIPE="$2"

    echo "Performing $CLEAN_ACTION on recipe: $RECIPE"

    case "$CLEAN_ACTION" in
        clean)
            bitbake -c clean "$RECIPE"
            ;;
        cleanall)
            bitbake -c cleanall "$RECIPE"
            ;;
        cleansstate)
            bitbake -c cleansstate "$RECIPE"
            ;;
    esac

    # Stop script after cleanup
    exit 0
fi

############################################################
# Define desired versions/tags/commits
#-----------------------------------------------
# Initialize and pin Yocto layers only once
#-----------------------------------------------
PROJECT_ROOT=$(pwd)
UPDATE_LAYERS=false

# Optional flag: ./build.sh --update to force layer sync
if [[ "$1" == "--update" ]]; then
    UPDATE_LAYERS=true
    echo "Forcing Yocto layer update..."
fi

# Define desired versions/tags/commits
POKY_TAG="yocto-4.0.29"
META_RPI_COMMIT="255500dd9f6a01a3445ac491d1abc401801e3bad"
META_OE_COMMIT="96fbc156364fd78530d2bfbe1b8a77789f52997d"

declare -A LAYER_VERSION_MAP=(
  [poky]="$POKY_TAG"
  [meta-raspberrypi]="$META_RPI_COMMIT"
  [meta-openembedded]="$META_OE_COMMIT"
)

# Initialize submodules only if missing
if [ ! -d "poky" ]; then
    echo "Initializing git submodules..."
    git submodule init
    git submodule update
else
    echo "✅ Submodules already initialized."
fi

# Loop through and pin each layer
for layer in "${!LAYER_VERSION_MAP[@]}"; do
    if [ -d "$layer" ]; then
        cd "$layer"
        current_commit=$(git rev-parse HEAD)
        target="${LAYER_VERSION_MAP[$layer]}"

        if $UPDATE_LAYERS; then
            echo "→ Updating $layer to $target ..."
            git fetch --all --tags
            git checkout "$target"
        else
            # Only checkout if current commit doesn't match
            if ! git merge-base --is-ancestor "$target" "$current_commit" 2>/dev/null; then
                echo "→ Checking out $layer to pinned version $target ..."
                git fetch --all --tags
                git checkout "$target"
            else
                echo "✅ $layer already at desired version ($target)"
            fi
        fi
        cd "$PROJECT_ROOT"
    else
        echo "⚠️  Layer $layer not found!"
    fi
done

###########################################################
# local.conf configuration

source poky/oe-init-build-env

CONFLINE="MACHINE = \"raspberrypi4-64\""

#Create image of the type rpi-sdimg
IMAGE="IMAGE_FSTYPES = \"wic.bz2\""

#Set GPU memory as minimum
MEMORY="GPU_MEM = \"16\""

#Licence
LICENSE="LICENSE_FLAGS_ACCEPTED = \"commercial\""

#----------------------------------------------------------
# CAN-related configuration for Waveshare RS485 CAN HAT Rev 2.1
# (12 MHz crystal, interrupt on GPIO25)
CAN_SPI='ENABLE_SPI_BUS = "1"'
CAN_DTO='RPI_EXTRA_CONFIG = "dtoverlay=mcp2515-can0,oscillator=12000000,interrupt=25,spimaxfrequency=5000000 dtoverlay=spi1-1cs"'
CAN_TOOLS='IMAGE_INSTALL:append = " can-utils iproute2 "'
CAN_INIT='IMAGE_INSTALL:append = " can-init "'

###########################################################

# Append all configuration entries if not already present in local.conf
for VAR in "$CONFLINE" "$IMAGE" "$MEMORY" "$LICENSE" "$CAN_SPI" "$CAN_DTO" "$CAN_TOOLS" "$CAN_INIT"; do
    if ! grep -q "$VAR" conf/local.conf; then
        echo "Appending $VAR"
        echo "$VAR" | tee -a conf/local.conf
    else
        echo "$VAR already exists."
    fi
done

#########################################################
# Add required layers
##########################################################

add_layer_if_missing "meta-raspberrypi" "../meta-raspberrypi"
add_layer_if_missing "meta-openembedded" "../meta-openembedded/meta-oe"
add_layer_if_missing "meta-ledhat" "../meta-ledhat"
add_layer_if_missing "meta-aesd" "../meta-aesd"
add_layer_if_missing "meta-can" "../meta-can"

###########################################################
# Show build configuration summary
echo "=============================================="
echo "Yocto build configuration summary:"
echo "Machine:    raspberrypi4-64"
echo "Layers:     meta-raspberrypi, meta-openembedded, meta-ledhat, meta-aesd, meta-can"
echo "Image type: wic.bz2"
echo "CAN overlay:     mcp2515-can0 @ 12 MHz (GPIO25 interrupt)"
echo "Init system:     BusyBox (/etc/init.d/S40can0)"
echo "=============================================="
# Show current layers
echo ""
echo "Current layers:"
bitbake-layers show-layers
###########################################################
# bitbake core-image-aesd
bitbake core-image-base
