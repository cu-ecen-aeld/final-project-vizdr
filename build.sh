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
# local.conf won't exist until this step on first execution
source poky/oe-init-build-env

CONFLINE="MACHINE = \"raspberrypi4-64\""

#Create image of the type rpi-sdimg
IMAGE="IMAGE_FSTYPES = \"wic.bz2\""

#Set GPU memory as minimum
MEMORY="GPU_MEM = \"16\""

#Licence
LICENCE="LICENSE_FLAGS_ACCEPTED = \"commercial\""

###########################################################

cat conf/local.conf | grep "${CONFLINE}" > /dev/null
local_conf_info=$?

cat conf/local.conf | grep "${IMAGE}" > /dev/null
local_image_info=$?

cat conf/local.conf | grep "${MEMORY}" > /dev/null
local_memory_info=$?

cat conf/local.conf | grep "${LICENCE}" > /dev/null
local_licn_info=$?

#########################################################
# Append necessary lines to local.conf if not already present

if [ $local_conf_info -ne 0 ];then
	echo "Append ${CONFLINE} in the local.conf file"
	echo "${CONFLINE}" | tee -a conf/local.conf
	
else
	echo "${CONFLINE} already exists in the local.conf file"
fi

if [ $local_image_info -ne 0 ];then 
    echo "Append ${IMAGE} in the local.conf file"
	echo ${IMAGE} >> conf/local.conf
else
	echo "${IMAGE} already exists in the local.conf file"
fi

# bitbake-layers show-layers | grep "meta-aesd" > /dev/null
# if [ $local_memory_info -ne 0 ];then
#     echo "Append ${MEMORY} in the local.conf file"
# 	echo ${MEMORY} >> conf/local.conf
# else
# 	echo "${MEMORY} already exists in the local.conf file"
# fi

if [ $local_memory_info -ne 0 ]; then
    echo "Append ${MEMORY} in the local.conf file"
    echo "${MEMORY}" | tee -a conf/local.conf
else
    echo "${MEMORY} already exists in the local.conf file"
fi


if [ $local_licn_info -ne 0 ];then
    echo "Append ${LICENCE} in the local.conf file"
	echo "${LICENCE}" | tee -a conf/local.conf
else
	echo "${LICENCE} already exists in the local.conf file"
fi

##########################################################

add_layer_if_missing "meta-raspberrypi" "../meta-raspberrypi"
add_layer_if_missing "meta-openembedded" "../meta-openembedded/meta-oe"
add_layer_if_missing "meta-ledhat" "../meta-ledhat"
add_layer_if_missing "meta-aesd" "../meta-aesd"

echo "=============================================="
echo "Yocto build configuration summary:"
echo "Machine:    raspberrypi4-64"
echo "Layers:     meta-raspberrypi, meta-openembedded, meta-ledhat, meta-aesd"
echo "Image type: wic.bz2"
echo "=============================================="
# Show current layers
echo ""
echo "Current layers:"
bitbake-layers show-layers
###########################################################
# bitbake core-image-aesd
bitbake core-image-base
