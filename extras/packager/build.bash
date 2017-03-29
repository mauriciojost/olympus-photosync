#!/bin/bash

set -e
set -x

INSTALLER_DIR=$(readlink -e `dirname $0`)
ROOT_DIR=$INSTALLER_DIR/../../

echo "### Building packages..."
cd $ROOT_DIR

sbt clean
sbt test
sbt universal:packageBin
sbt universal:packageZipTarball
sbt debian:packageBin
sbt rpm:packageBin
#sbt windows:packageBin

cd $INSTALLER_DIR

echo "### Locating packages..."
find $ROOT_DIR/target -name *.zip
find $ROOT_DIR/target -name *.tgz
find $ROOT_DIR/target -name *.deb
find $ROOT_DIR/target -name *.rpm
#find $ROOT_DIR/target -name *.exe

echo "### Done."
