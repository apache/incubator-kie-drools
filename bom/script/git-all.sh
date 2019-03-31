#!/bin/bash

# Runs a git command on all kiegroup repositories.

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

# default repository list is stored in the repository-list.txt file
REPOSITORY_LIST=`cat "${scriptDir}/repository-list.txt"`
GIT_ARG_LINE=()

for arg in "$@"
do
    case "$arg" in
        --target-repo=*)
            REPOSITORY_LIST=$($scriptDir/checks/repo-dep-tree.pl -w -t ${arg#*=})
            REPOSITORY_LIST=${REPOSITORY_LIST//,/ }
        ;;

        --repo-list=*)
            REPOSITORY_LIST=$(echo "$arg" | sed 's/[-a-zA-Z0-9]*=//')
            # replace the commas with spaces so that the for loop treats the individual repos as different values
            REPOSITORY_LIST=${REPOSITORY_LIST//,/ }
        ;;

        *)
            GIT_ARG_LINE+=("$arg")
        ;;
    esac
done

if [ "x$GIT_ARG_LINE" = "x" ] ; then
    echo
    echo "Usage:"
    echo "  $0 <arguments of git> [--repo-list=<list-of-repositories>]"
    echo "For example:"
    echo "  $0 fetch"
    echo "  $0 pull --rebase"
    echo "  $0 commit -m\"JIRAKEY-1 Fix typo\""
    echo "  $0 checkout master --repo-list=drools,jbpm"
    echo
    exit 1
fi

startDateTime=`date +%s`

echo "Git arg. line=${GIT_ARG_LINE[@]}"
cd "$droolsjbpmOrganizationDir"

for repository in $REPOSITORY_LIST ; do
    echo
    if [ ! -d "$droolsjbpmOrganizationDir/$repository" ]; then
        echo "==============================================================================="
        echo "Missing Repository: $repository. SKIPPING!"
        echo "==============================================================================="
    else
        echo "==============================================================================="
        echo "Repository: $repository"
        echo "==============================================================================="
        cd $repository

        git "${GIT_ARG_LINE[@]}"

        returnCode=$?
        cd ..
        if [ $returnCode != 0 ] ; then
            echo -n "Error executing command for repository ${repository}. Should I continue? (Hit control-c to stop or enter to continue): "
            read ok
        fi
    fi
done

endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`

echo
echo "Total time: ${spentSeconds}s"
