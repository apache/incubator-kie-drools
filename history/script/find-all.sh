#!/bin/bash

# Run a mvn command on all droolsjbpm repositories.

initializeWorkingDirAndScriptDir() {
    # Set working directory and remove all symbolic links
    workingDir=`pwd -P`

    # Go the script directory
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
initializeWorkingDirAndScriptDir
droolsjbpmOrganizationDir="$scriptDir/../.."

if [ $# = 0 ] ; then
    echo
    echo "Usage:"
    echo "  $0 [filename] [string to find]"
    echo "For example:"
    echo "  $0 pom.xml commons-math"
    echo "    Finds all modules that depend on commons-math"
    echo "  $0 *.java org.apache.commons.math"
    echo "    Finds all java code that imports from commons-math"
    echo
    exit 1
fi

cd "$droolsjbpmOrganizationDir"

find . -type f -name "$1" -not -path '*/target/*' -not -path '*/.git/*' -prune -exec grep -H "$2" '{}' \;
