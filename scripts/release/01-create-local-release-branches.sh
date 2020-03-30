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

if [ $# != 1 ] && [ $# != 2 ] && [ $# != 3 ] ; then
    echo
    echo "Usage:"
    echo "  $0 ReleaseBranchName BaseBranchName"
    echo "For example:"
    echo "  $0 0.1.x master"
    echo
    exit 1
fi

echo "Kogito release version name is $1"

echo -n "Is this ok? (Hit control-c if is not): "
read ok

startDateTime=`date +%s`

cd $organizationDir

for repository in `cat $organizationDir/kogito-runtimes/scripts/repository-list.txt` ; do
    echo
    if [ ! -d $organizationDir/$repository ]; then
        echo "==============================================================================="
        echo "Missing Repository: $repository. SKIPPING!"
        echo "==============================================================================="  
    else
        echo "==============================================================================="
        echo "Repository: $repository"
        echo "==============================================================================="
        cd $repository

        releaseBranchName=r$1
        baseBranchName=$2
        
        if [ $# == 3 ] && [ $3 != "" ]; then
          targetBranchName=$3
          git checkout -b $targetBranchName $baseBranchName
        fi
        
        git checkout -b $releaseBranchName $baseBranchName
        
        returnCode=$?
        cd ..
        if [ $returnCode != 0 ] ; then
            exit $returnCode
        fi
    fi
done

endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`

echo
echo "Total time: ${spentSeconds}s"
echo "Warning: your working branches are now those release branches, NOT master."