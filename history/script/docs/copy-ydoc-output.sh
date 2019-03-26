#!/bin/bash

# Copies the ydoc output into the drools-docs.
# For more information, see kiegroup/droolsjbpm-knowledge/knowledge-api/pom.xml#maven-javadoc-plugin

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

cd $droolsjbpmOrganizationDir

sourceResource="null"
destinationResource="null"
for line in `cat ${scriptDir}/ydoc-resources-list.txt` ; do
    if [ $sourceResource == "null" ] ; then
        sourceResource=$line
    elif [ $destinationResource == "null" ] ; then
        destinationResource=$line
        echo "Copying sourceResource ($sourceResource) to destinationResource ($destinationResource)..."
        if [ ! -f $droolsjbpmOrganizationDir/$sourceResource ]; then
            echo "The sourceResource ($sourceResource) does not exist."
            exit 1
        fi
        cp $droolsjbpmOrganizationDir/$sourceResource $droolsjbpmOrganizationDir/$destinationResource

        sourceResource="null"
        destinationResource="null"
    else
        echo "Could not process line ($line)."
        exit 1
    fi
done

if [ $sourceResource != "null" ] ; then
    echo "The sourceResource ($sourceResource) is not processed."
    exit 1
fi

echo "SCRIPT SUCCESS"
