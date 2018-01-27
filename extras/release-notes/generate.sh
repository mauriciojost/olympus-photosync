#!/bin/bash


git tag | sort -t . -n -k 2 > all.tags

echo "All tags are:"
cat all.tags

rnfile=$1

rm -f $rnfile

function release(){
  local file=$1
  local from=$2
  local to=$3
  echo "" >> $file
  echo "" >> $file
  echo "## RELEASE: $to" >> $file
  echo "" >> $file
  git log $from...$to --pretty=format:'commit %s' --reverse | \
    grep -v .gitignore | \
    grep -vi README | \
    grep -vi TODO | \
    grep -vi MOVE | \
    grep -vi TEST | \
    grep -vi INDENTATION  >> $file
}


echo "# RELEASE NOTES" >> $rnfile
release $rnfile v0.1 v0.2
release $rnfile v0.2 v0.3
release $rnfile v0.3 v0.4
release $rnfile v0.4 v0.5
release $rnfile v0.5 v0.6
release $rnfile v0.6 v0.7
release $rnfile v0.7 v0.8
release $rnfile v0.8 v0.9
release $rnfile v0.9 v0.10
release $rnfile v0.10 v0.11
release $rnfile v0.11 v0.12
release $rnfile v0.12 v0.13

