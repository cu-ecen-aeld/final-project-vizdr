#!/bin/bash
# Script to build image for raspberry pi 4 using Yocto Project
# Author: Siddhant Jajoo, Vladimir Zdravkov.

# Add required layers

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

if [ "$1" = "clean" ]; then
    echo "Cleaning Yocto build directories..."
    rm -rf tmp sstate-cache downloads cache
    echo "Cleanup complete."
    exit 0
fi


PROJECT_ROOT="$(pwd)"

set -e

git submodule init
git submodule sync
git submodule update

# Ensure all submodules are on kirkstone branch
for layer in poky meta-raspberrypi meta-openembedded; do
    if [ -d "$layer" ]; then
        echo "Checking out $layer to kirkstone branch..."
        cd "$layer"
        git fetch --all --tags
        git checkout kirkstone || git checkout -b kirkstone origin/kirkstone
        git pull origin kirkstone || true
        cd "$PROJECT_ROOT"
    fi
done

############################################################

# local.conf configuraion
source poky/oe-init-build-env

CONFLINE="MACHINE = \"raspberrypi4-64\""

#Create image of the type rpi-sdimg
IMAGE="IMAGE_FSTYPES = \"wic.bz2\""

#Set GPU memory as minimum
MEMORY="GPU_MEM = \"16\""

#Licence
LICENCE="LICENSE_FLAGS_ACCEPTED = \"commercial\""

#----------------------------------------------------------
# CAN-related configuration for Waveshare RS485 CAN HAT Rev 2.1
# (12 MHz crystal, interrupt on GPIO25)
CAN_SPI='ENABLE_SPI_BUS = "1"'
CAN_DTO='APPEND += " dtoverlay=mcp2515-can0,oscillator=12000000,interrupt=25 dtoverlay=spi1-1cs "'
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
