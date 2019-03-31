#!/bin/bash

# Git clone the other repositories

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

startDateTime=`date +%s`

# The gitUrlPrefix differs between committers and anonymous users. Also it differs on forks.
# Committers on blessed gitUrlPrefix="git@github.com:kiegroup/"
# Anonymous users on blessed gitUrlPrefix="git://github.com/kiegroup/"
cd "${scriptDir}"
droolsjbpmGitUrlPrefix=`git remote -v | grep --regex "^origin.*(fetch)$"`
droolsjbpmGitUrlPrefix=`echo ${droolsjbpmGitUrlPrefix} | sed 's/^origin\s*//g' | sed 's/droolsjbpm\-build\-bootstrap.*//g'`

cd "$droolsjbpmOrganizationDir"

# additinal Git options can be passed simply as params to the script
# example: --depth 1 (creates a shallow clone with that depth)
additionalGitOptions="$@"

for repository in `cat "${scriptDir}/repository-list.txt"` ; do
    echo
    if [ -d $repository ] ; then
        echo "==============================================================================="
        echo "This directory already exists: $repository"
        echo "==============================================================================="
    else
        echo "==============================================================================="
        echo "Repository: $repository"
        echo "==============================================================================="
        gitUrlPrefix=${droolsjbpmGitUrlPrefix}
        echo -- prefix ${gitUrlPrefix} --
        echo -- repository ${repository} --
        echo -- ${gitUrlPrefix}${repository}.git -- ${repository} --
        if [ "x${additionalGitOptions}" != "x" ]; then
            echo -- additional Git options: ${additionalGitOptions} --
        fi
        git clone ${additionalGitOptions} ${gitUrlPrefix}${repository}.git ${repository}

        returnCode=$?
        if [ $returnCode != 0 ] ; then
            exit $returnCode
        fi
    fi
done

echo
echo Disk size:

for repository in `cat "${scriptDir}/repository-list.txt"` ; do
    du -sh $repository
done

endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`

echo
echo "Total time: ${spentSeconds}s"
