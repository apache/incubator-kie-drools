#!/bin/bash
set -e

# Updates the version for all kogito repositories

mvnVersionsSet() {
    mvn -B -N -e versions:set -Dfull\
      -DnewVersion="$newVersion" -DallowSnapshots=true -DgenerateBackupPoms=false
}

mvnVersionsUpdateParent() {
    mvn -B -N -e versions:update-parent -Dfull\
     -DparentVersion="[$newVersion]" -DallowSnapshots=true -DgenerateBackupPoms=false
}

mvnVersionsUpdateChildModules() {
    mvn -B -N -e versions:update-child-modules -Dfull\
     -DallowSnapshots=true -DgenerateBackupPoms=false
}

# Updates parent version and child modules versions for Maven project in current working dir
mvnVersionsUpdateParentAndChildModules() {
    mvnVersionsUpdateParent
    mvnVersionsUpdateChildModules
}

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
echo "New version is $newVersion"

releaseType=$2
# check if the release type was set, if not default to "community"
if [ "x$releaseType" == "x" ]; then
    releaseType="community"
fi


echo "Specified release type: $releaseType"


startDateTime=`date +%s`

cd $organizationDir

for repository in `cat kogito-runtimes/scripts/repository-list.txt` ; do
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

        if [ "$repository" == "kogito-runtimes" ]; then
            # first build&install the current version (usually SNAPSHOT) as it is needed later by other repos
            mvn -B -U -Dfull clean install
            mvnVersionsSet
            sed -i -E "s/<version\.org\.kie\.kogito>.*<\/version\.org\.kie\.kogito>/<version.org.kie.kogito>$newVersion<\/version.org.kie.kogito>/" pom.xml
            # workaround for http://jira.codehaus.org/browse/MVERSIONS-161
            mvn -B clean install -DskipTests
            returnCode=$?

        else
            mvnVersionsUpdateParentAndChildModules
            returnCode=$?
        fi
        if [ $returnCode != 0 ] ; then
            exit $returnCode
        fi
        cd ..
    fi
done
endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`
echo
echo "Total time: ${spentSeconds}s"