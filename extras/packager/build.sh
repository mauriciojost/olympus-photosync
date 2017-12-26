#!/bin/bash

set -e
set -x

installer_dir=$(readlink -e `dirname $0`)
root_dir=$installer_dir/../../
src_dir=$root_dir/src

echo "### Notifying about versions match..."
version_constants=`find $src_dir | grep Constants.scala | xargs cat | grep Version`
version_sbt=`find $root_dir | grep version.sbt | xargs cat`
echo ""
echo "Versions are: $version_constants and $version_sbt"
echo ""
sleep 6

echo "### Building packages..."
cd $root_dir

sbt clean
sbt test
sbt universal:packageBin
sbt universal:packageZipTarball
sbt debian:packageBin
sbt rpm:packageBin
#sbt windows:packageBin

cd $installer_dir

rm -f $root_dir/packages.log
rm -f $root_dir/packages.md5sum

echo "### Locating packages..."
find $root_dir/target -name *.zip >> $root_dir/packages.log
find $root_dir/target -name *.tgz >> $root_dir/packages.log
find $root_dir/target -name *.deb >> $root_dir/packages.log
find $root_dir/target -name *.rpm >> $root_dir/packages.log
#find $root_dir/target -name *.exe >> $root_dir/packages.log

cat $root_dir/packages.log | xargs -I% md5sum % >> $root_dir/packages.md5sum

echo "### Packages generated:"
cat $root_dir/packages.log
cat $root_dir/packages.md5sum

echo "### Done."
