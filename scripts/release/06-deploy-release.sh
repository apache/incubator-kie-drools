#!/bin/bash

# Create a release branch for all git repositories

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

organizationDir="$scriptDir/../../.."

if [ $# != 1 ]; then
    echo
    echo "Usage:"
    echo "  $0 path to settings.xml"
    echo "For example:"
    echo "  $0 /path/settings.xml"
    echo
    exit 1
fi

echo "Kogito release about to be deployed..."

echo -n "Is this ok? (Hit control-c if is not): "
read ok

startDateTime=`date +%s`

$organizationDir/kogito-runtimes/scripts/mvn-all.sh -B -e -U clean deploy -DskipTests -Dmaven.repo.local=/tmp/kogito-release-repo -s $1

rm -rf /tmp/kogito-release-repo

endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`

echo
echo "Total time: ${spentSeconds}s"