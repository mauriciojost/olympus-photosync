#!/bin/bash

installer_dir=$(readlink -e `dirname $0`)
root_dir=$installer_dir/../../
src_dir=$root_dir/src

release_version=$1
previous_release_version=$2

echo "Example of use: bash build.sh 0.15.0 0.14.0 # bash build.sh new_release old_release"
echo "Generating release: v$release_version"
echo "Previous release: v$previous_release_version"

sleep 5

echo ""
echo "### 1. Update versions"

find $root_dir | grep Constants.scala | xargs -I% sed -i "s/1master/$release_version/g" %
find $root_dir | grep version.sbt | xargs -I% sed -i "s/1master/$release_version/g" %
find $root_dir | grep README.md | xargs -I% sed -i "s/1master/$release_version/g" %

version_constants=`find $src_dir | grep Constants.scala | xargs cat | grep Version`
version_sbt=`find $root_dir | grep version.sbt | xargs cat`
echo "Version should be of the form: $release_verion"
echo "Currently are: $version_constants and $version_sbt"
echo "Update in:"
find $root_dir | grep Constants.scala
find $root_dir | grep version.sbt
find $root_dir | grep README.md
echo ""

echo "### 2. Update release notes"

rnfile=$root_dir/RELEASE-NOTES.md

function release(){
  local from=$1
  local to=$2
  echo ""
  echo ""
  echo "## RELEASE: $release_version"
  echo ""
  git log $from...$to --pretty=format:'commit %s' --reverse | \
    grep -v .gitignore | \
    grep -vi README | \
    grep -vi TODO | \
    grep -vi MOVE | \
    grep -vi TEST | \
    grep -vi INDENTATION
}

release $previous_release_version HEAD >> $rnfile 


echo "### 3. Create releases..."

sleep 6

set -e
set -x


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

echo "### 4. Review and create commit"

echo "### 5. Tag commit: git tag -a $release_version"

echo "### 6. Create release in github and upload release packages with release notes"



echo "### Done."
