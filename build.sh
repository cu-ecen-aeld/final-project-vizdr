#!/bin/bash
# Script to build image for qemu.
# Author: Siddhant Jajoo.
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

# local.conf won't exist until this step on first execution
source poky/oe-init-build-env

CONFLINE="MACHINE = \"raspberrypi4-64\""

#Create image of the type rpi-sdimg
IMAGE="IMAGE_FSTYPES = \"wic.bz2\""

#Set GPU memory as minimum
MEMORY="GPU_MEM = \"16\""

#Licence
LICENCE="LICENSE_FLAGS_ACCEPTED = \"commercial\""

cat conf/local.conf | grep "${CONFLINE}" > /dev/null
local_conf_info=$?

cat conf/local.conf | grep "${IMAGE}" > /dev/null
local_image_info=$?

cat conf/local.conf | grep "${MEMORY}" > /dev/null
local_memory_info=$?

cat conf/local.conf | grep "${LICENCE}" > /dev/null
local_licn_info=$?


if [ $local_conf_info -ne 0 ];then
	echo "Append ${CONFLINE} in the local.conf file"
	echo ${CONFLINE} >> conf/local.conf
	
else
	echo "${CONFLINE} already exists in the local.conf file"
fi

if [ $local_image_info -ne 0 ];then 
    echo "Append ${IMAGE} in the local.conf file"
	echo ${IMAGE} >> conf/local.conf
else
	echo "${IMAGE} already exists in the local.conf file"
fi

bitbake-layers show-layers | grep "meta-aesd" > /dev/null
if [ $local_memory_info -ne 0 ];then
    echo "Append ${MEMORY} in the local.conf file"
	echo ${MEMORY} >> conf/local.conf
else
	echo "${MEMORY} already exists in the local.conf file"
fi

if [ $local_licn_info -ne 0 ];then
    echo "Append ${LICENCE} in the local.conf file"
	echo ${LICENCE} >> conf/local.conf
else
	echo "${LICENCE} already exists in the local.conf file"
fi


bitbake-layers show-layers | grep "meta-raspberrypi" > /dev/null
layer_info=$?


if [ $layer_info -ne 0 ];then
	echo "Adding meta-aesd layer"
	bitbake-layers add-layer ../meta-aesd
	echo "Adding meta-raspberrypi layer"
	bitbake-layers add-layer ../meta-raspberrypi
else
	echo "meta-aesd layer already exists"
	echo "layer meta-raspberrypi already exists"
fi


bitbake core-image-aesd
bitbake core-image-base
