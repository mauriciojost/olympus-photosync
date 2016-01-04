#!/bin/bash

set -e
set -x

INSTALLER_DIR=$(readlink -e `dirname $0`)
ROOT_DIR=$INSTALLER_DIR/../../

NAME=photosync
VERSION=`cat ../../build.sbt | grep version | awk '{print $3}' | sed "s/\"//g"`
MANTAINER="Mauricio Jost <mauriciojostx@gmail.com>"
OUTPUT_TYPES="deb rpm"
OPT_BASE_DIR=mauritania
DESCRIPTION="Photosync allows to syncrhonize media from Olympus cameras to a PC wirelessly."

TMP_DIR=tmp
INPUT_DIR=input
OUTPUT_DIR=output


echo "### Building default zip package..."
cd $ROOT_DIR
sbt universal:packageBin
cd $INSTALLER_DIR

echo "### Cleaning directories..."
mkdir -p $OUTPUT_DIR $TMP_DIR
rm -fr $OUTPUT_DIR/* $TMP_DIR/*

echo "### Unzipping source package..."
unzip -d $TMP_DIR $ROOT_DIR/target/universal/$NAME-$VERSION.zip

echo "### Preparing sources from input package..."
mkdir -p $TMP_DIR/opt/$OPT_BASE_DIR/
mv $TMP_DIR/$NAME-$VERSION $TMP_DIR/opt/$OPT_BASE_DIR/$NAME

echo "### Preparing sources from added resources..."
cp -r $INPUT_DIR/package/* $TMP_DIR/
chmod a+rwx $TMP_DIR/var/log/$NAME

echo "### Preparing configuration files..."
cp -r $ROOT_DIR/src/main/resources/application.conf $TMP_DIR/etc/photosync/

echo "### Creating target packages..."
for target_type in $OUTPUT_TYPES
do

  echo "## Creating $target_type ..."

  fpm -s dir -C $TMP_DIR \
    -t $target_type -a all \
    -n $NAME -m "$MANTAINER" \
    -v $VERSION -p $OUTPUT_DIR \
    --description "$DESCRIPTION" \
    --depends openjdk-7-jre \
    --after-install $INPUT_DIR/scripts/after-install.sh \
    --before-remove $INPUT_DIR/scripts/before-remove.sh \
    etc opt usr var

done

echo "### Done."
