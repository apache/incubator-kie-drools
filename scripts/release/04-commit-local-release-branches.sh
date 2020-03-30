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


if [ $# != 1 ] && [ $# != 2 ] ; then  # && [ $# != 3 ] ; then
    echo
    echo "Usage:"
    echo "  $0 ReleaseBranchName"
    echo "For example:"
    echo "  $0 0.1.x"
    echo
    exit 1
fi

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

		releaseBranchName=$1
		#add all changed files and commit with release message
		git add -u
		git commit -m "Release of version $releaseBranchName"
        
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