#!/bin/bash

# Run a mvn command on all kiegroup repositories.

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

printUsage() {
    echo
    echo "Usage:"
    echo "  $0 <Maven arguments> [--repo-list=<list-of-repositories>|--target-repo=<repository>] [--clean-up-script=<absolute-path>]"
    echo "For example:"
    echo "  $0 --version"
    echo "  $0 -DskipTests clean install"
    echo "  $0 -Dfull clean install"
    echo "  $0 clean test --repo-list=drools,jbpm"
    echo "  $0 clean test --clean-up-script=\`pwd\`/remove-big-dirs.sh"
    echo "  $0 clean install --target-repo=drools-wb"
    echo
}

initializeWorkingDirAndScriptDir
droolsjbpmOrganizationDir="$scriptDir/../.."

# default repository list is stored in the repository-list.txt file
REPOSITORY_LIST=`cat "${scriptDir}/repository-list.txt"`
MVN_ARG_LINE=()

for arg in "$@"
do
    case "$arg" in
        --target-repo=*)
            REPOSITORY_LIST=$($scriptDir/checks/repo-dep-tree.pl -w -t ${arg#*=})
            REPOSITORY_LIST=${REPOSITORY_LIST//,/ }
        ;;

        --repo-list=*)
            REPOSITORY_LIST=$(echo $arg | sed 's/[-a-zA-Z0-9]*=//')
            # replace the commas with spaces so that the for loop treats the individual repos as different values
            REPOSITORY_LIST=${REPOSITORY_LIST//,/ }
        ;;

        --clean-up-script=*)
            CLEAN_UP_SCRIPT=$(echo $arg | sed 's/[-a-zA-Z0-9]*=//')
        ;;

        *)
            MVN_ARG_LINE+=("$arg")
        ;;
    esac
done


# check that Maven args are non empty
if [ "$MVN_ARG_LINE" = "" ] ; then
    echo "No Maven arguments specified!"
    printUsage
    exit 1
fi

# check the clean-up script exists (if specified via arg)
if [ "$CLEAN_UP_SCRIPT" != "" ] && [ ! -f "$CLEAN_UP_SCRIPT" ]; then
    echo "Invalid clean-up script specified: $CLEAN_UP_SCRIPT. The file does not exist!"
    printUsage
    exit 2
fi

mvnBin="mvn"
if [ -a $M3_HOME/bin/mvn ] ; then
    mvnBin="$M3_HOME/bin/mvn"
fi
echo "Using Maven binary '$mvnBin'"
"$mvnBin" -v
echo

echo "Maven arg. line=${MVN_ARG_LINE[@]}"
if [ "$CLEAN_UP_SCRIPT" != "" ]; then
    echo "Using clean-up script '$CLEAN_UP_SCRIPT'"
fi

cd "$droolsjbpmOrganizationDir"
startDateTime=`date +%s`

for repository in $REPOSITORY_LIST; do
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

        "$mvnBin" "${MVN_ARG_LINE[@]}"
        returnCode=$?

        if [ $returnCode != 0 ] ; then
            exit $returnCode
        fi
        if [ "$CLEAN_UP_SCRIPT" != "" ]; then
            echo "Running clean-up script $CLEAN_UP_SCRIPT for repository $repository"
            sh "$CLEAN_UP_SCRIPT" `pwd`
        fi
        cd ..
    fi
done

endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`

echo
echo "Total time: ${spentSeconds}s"
