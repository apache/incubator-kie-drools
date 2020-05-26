#!/bin/bash
set -e

initializeScriptDir() {
    # Go to the script directory
    cd `dirname $0`
    # If the file itself is a symbolic link (ignoring parent directory links), then follow that link recursively
    # Note that scriptDir=`pwd -P` does not do that and cannot cope with a link directly to the file
    scriptFileBasename=`basename $0`
    while [ -L "$scriptFileBasename" ] ; do
        scriptFileBasename=`readlink $scriptFileBasename` # Follow the link
        cd `dirname $scriptFileBasename`
        scriptFileBasename=`basename $scriptFileBasename`
    done
    # Set script directory and remove other symbolic links (parent directory links)
    scriptDir=`pwd -P`
}

initializeScriptDir

if [ $# != 1 ] && [ $# != 2 ]; then
    echo
    echo "Usage:"
    echo "  $0 newVersion releaseType"
    echo "For example:"
    echo "  $0 7.5.0.Final community"
    echo
    exit 1
fi

newVersion=$1

for file in */pom.xml ; do
  echo Updating $file to $newVersion...
  sed -i "s/<version\.org\.optaplanner>.*<\/version\.org\.optaplanner>/<version.org.optaplanner>$newVersion<\/version.org.optaplanner>/g" $file
  if [ `grep "<version.org.optaplanner>$newVersion</version.org.optaplanner>" $file | wc -l` != 1 ]; then
    echo "ERROR updating $file"
    exit 1
  fi
done
for file in */build.gradle ; do
  echo Updating $file to $newVersion...
  sed -i "s/def optaplannerVersion = \".*\"/def optaplannerVersion = \"$newVersion\"/g" $file
  if [ `grep "def optaplannerVersion = \"$newVersion\"" $file | wc -l` != 1 ]; then
    echo "ERROR updating $file"
    exit 1
  fi
done
