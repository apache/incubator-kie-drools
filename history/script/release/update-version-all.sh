#!/bin/bash
set -e

# IMPORTANT: if you want the script to use a custom settings.xml instead of a predefined (community or productized)
# set the variable SETTINGS_XML_FILE with the full path to your settings.xml like
#
# export SETTINGS_XML_FILE="<full path>/settings.xml"
#
# and run the script > sh update-version-all.sh newVersion newAppformerVersion custom.


# Updates the version for all kiegroup repositories

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

mvnVersionsSet() {
    mvn -B -N -e -s $settingsXmlFile versions:set -Dfull\
      -DnewVersion="$newVersion" -DallowSnapshots=true -DgenerateBackupPoms=false
}

mvnVersionsUpdateParent() {
    mvn -B -N -e -s $settingsXmlFile versions:update-parent -Dfull\
     -DparentVersion="[$newVersion]" -DallowSnapshots=true -DgenerateBackupPoms=false
}

mvnVersionsUpdateChildModules() {
    mvn -B -N -e -s $settingsXmlFile versions:update-child-modules -Dfull\
     -DallowSnapshots=true -DgenerateBackupPoms=false
}

# Updates parent version and child modules versions for Maven project in current working dir
mvnVersionsUpdateParentAndChildModules() {
    mvnVersionsUpdateParent
    mvnVersionsUpdateChildModules
}

initializeScriptDir
droolsjbpmOrganizationDir="$scriptDir/../../.."

if [ $# != 1 ] && [ $# != 3 ]; then
    echo
    echo "Usage:"
    echo "  $0 newVersion newAppformerVersion releaseType"
    echo "For example:"
    echo "  $0 7.5.0.Final 2.2.0.Final community"
    echo "  $0 7.5.0.20171120-prod 2.2.0.20171120-prod productized"
    echo
    exit 1
fi

newVersion=$1
echo "New version is $newVersion"

releaseType=$3
# check if the release type was set, if not default to "community"
if [ "x$releaseType" == "x" ]; then
    releaseType="community"
fi


if [ $releaseType == "community" ]; then
    settingsXmlFile="$scriptDir/update-version-all-community-settings.xml"
elif [ $releaseType == "productized" ]; then
    settingsXmlFile="$scriptDir/update-version-all-productized-settings.xml"
elif [ $releaseType == "custom" ]; then
    settingsXmlFile="$SETTINGS_XML_FILE"
else
    echo "Incorrect release type specified: '$releaseType'. Supported values are 'community' or 'productized' or 'custom'"
    exit 1
fi
echo "Specified release type: $releaseType"
echo "Using following settings.xml: $settingsXmlFile"


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

        if [ "$repository" == "lienzo-core" ]; then
            mvnVersionsSet
            returnCode=$?

        elif [ "$repository" == "lienzo-tests" ]; then
            mvnVersionsSet
            returnCode=$?

        elif [ "$repository" == "kie-soup" ]; then
            mvnVersionsSet
            cd kie-soup-bom
            mvnVersionsSet
            cd ..
            mvn -B -U -Dfull -s $settingsXmlFile clean install -DskipTests
            returnCode=$?

        elif [ "$repository" == "appformer" ]; then
            #appformer has its own version
            # newVersion is updated with newVersion for appformer
            newVersion=$2
            mvnVersionsSet
            cd uberfire-bom
            mvnVersionsSet
            cd ..
            mvn -B -U -Dfull -s $settingsXmlFile clean install -DskipTests
            # switch back to kie version
            newVersion=$1
            returnCode=$?

        elif [ "$repository" == "droolsjbpm-build-bootstrap" ]; then
            # first build&install the current version (usually SNAPSHOT) as it is needed later by other repos
            mvn -B -U -Dfull -s $settingsXmlFile clean install
            mvnVersionsSet
            sed -i "s/<version\.org\.kie>.*<\/version.org.kie>/<version.org.kie>$newVersion<\/version.org.kie>/" pom.xml
            # update latest released version property only for non-SNAPSHOT versions
            if [[ ! $newVersion == *-SNAPSHOT ]]; then
                sed -i "s/<latestReleasedVersionFromThisBranch>.*<\/latestReleasedVersionFromThisBranch>/<latestReleasedVersionFromThisBranch>$newVersion<\/latestReleasedVersionFromThisBranch>/" pom.xml
            fi
            # update version also for user BOMs, since they do not use the top level kie-parent
            cd kie-user-bom-parent
            mvnVersionsSet
            cd ..
            # workaround for http://jira.codehaus.org/browse/MVERSIONS-161
            mvn -B -s $settingsXmlFile clean install -DskipTests
            returnCode=$?

        elif [ "$repository" == "drlx-parser" ];then
            #update version in drlx-parser/pom.xml
            cd drlx-parser
            mvnVersionsUpdateParent
            cd ..
            returnCode=$?

        elif [ "$repository" == "jbpm" ]; then
            mvnVersionsUpdateParentAndChildModules
            returnCode=$?
            sed -i "s/release.version=.*$/release.version=$newVersion/" jbpm-installer/src/main/resources/build.properties

        elif [ "$repository" == "droolsjbpm-tools" ]; then
            cd drools-eclipse
            mvn -B -s $settingsXmlFile -Dfull tycho-versions:set-version -DnewVersion=$newVersion
            returnCode=$?
            # replace the leftovers not covered by the tycho plugin (bug?)
            # SNAPSHOT and release versions need to be handled differently
            versionToUse=$newVersion
            if [[ $newVersion == *-SNAPSHOT ]]; then
                versionToUse=`sed "s/-SNAPSHOT/.qualifier/" <<< $newVersion`
            fi
            sed -i "s/source_[^\"]*/source_$versionToUse/" org.drools.updatesite/category.xml
            sed -i "s/version=\"[^\"]*\">/version=\"$versionToUse\">/" org.drools.updatesite/category.xml
            cd ..
            if [ $returnCode == 0 ]; then
                mvn -B -N -s $settingsXmlFile clean install
                mvnVersionsUpdateParent
                # workaround for http://jira.codehaus.org/browse/MVERSIONS-161
                mvn -B -N -s $settingsXmlFile clean install -DskipTests
                cd drools-eclipse
                mvnVersionsUpdateParent
                cd ..
                mvnVersionsUpdateChildModules
                returnCode=$?
            fi

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
