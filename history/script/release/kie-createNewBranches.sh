!/bin/bash

# Create a new branch for all git repositories

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
droolsjbpmOrganizationDir="$scriptDir/../../.."

if [ $# != 1 ] && [ $# != 2 ] ; then  # && [ $# != 3 ] ; then
    echo
    echo "Usage:"
    echo "  $0 NewBranchName"
    echo "For example:"
    echo "  $0 7.6.x"
    echo
    exit 1
fi

echo "The drools, guvnor, ... adn jbpm release branch name is $1"
echo -n "Is this ok? (Hit control-c if is not): "
read ok

startDateTime=`date +%s`

cd $droolsjbpmOrganizationDir

for repository in `cat ${scriptDir}/../repository-list.txt` ; do
    echo
    if [ ! -d $droolsjbpmOrganizationDir/$repository ]; then
        echo "==============================================================================="
        echo "Missing Repository: $repository. SKIPPING!"
        echo "==============================================================================="
    else
        echo "==============================================================================="
        echo "Repository: $repository"
        echo "==============================================================================="
        cd $repository

        newBranchName=$1
        git checkout -b $newBranchName
        git push origin $newBranchName
        # Set up the local branch to track the remote branch
        git branch --set-upstream $newBranchName origin/$newBranchName

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