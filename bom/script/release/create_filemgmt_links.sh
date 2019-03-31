#!/bin/bash

# Update the symbolic links lastest and latestFinal in 
# drools@filemgmt.jboss.org:downloads_htdocs/drools/release/
# drools@filemgmt.jboss.org:downloads_htdocs/docs_htdocs/drools/release/
# optaplanner@filemgmt.jboss.org:downloads_htdocs/optaplanner/release/
# optaplanner@filemgmt.jboss.org:downloads_htdocs/docs_htdocs/optaplanner/release/
# jbpm@filemgmt.jboss.org:downloads_htdocs/jbpm/release/
# jbpm@filemgmt.jboss.org:downloads_htdocs/docs_htdocs/jbpm/release/

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


if [ $# != 1 ] && [ $# != 2 ] ; then
    echo
    echo "Usage:"
    echo "  $0 kie version"
    echo "For example:"
    echo "  $0 6.1.0.Final"
    echo
    exit 1
fi

kieVersion=$1

echo "The kie-version: ${kieVersion}"
echo -n "Is this ok? (Hit control-c if is not): "
read ok

cd $droolsjbpmOrganizationDir
mkdir filemgmt_links
cd filemgmt_links

urlBase_drools="drools@filemgmt.jboss.org:"

###############################################################################
# latest drools links
###############################################################################
touch ${kieVersion}
rm latest
ln -s ${kieVersion} latest

echo "Uploading normal links..."
rsync -a --protocol=28 latest $urlBase_drools/downloads_htdocs/drools/release/
rsync -a --protocol=28 latest $urlBase_drools/docs_htdocs/drools/release/

###############################################################################
# latestFinal drools links
###############################################################################
if [[ "${kieVersion}" == *Final* ]]; then
    rm latestFinal
    ln -s ${kieVersion} latestFinal
    echo "Uploading Final links..."
    rsync -a --protocol=28  latestFinal $urlBase_drools/downloads_htdocs/drools/release/
    rsync -a --protocol=28  latestFinal $urlBase_drools/docs_htdocs/drools/release/
fi

urlBase_optaplanner="optaplanner@filemgmt.jboss.org:"

###############################################################################
# latest optaplanner links
###############################################################################
touch ${kieVersion}
rm latest
ln -s ${kieVersion} latest

echo "Uploading normal links for optaplanner..."
rsync -a --protocol=28 latest $urlBase_optaplanner/downloads_htdocs/optaplanner/release/
rsync -a --protocol=28 latest $urlBase_optaplanner/docs_htdocs/optaplanner/release/


###############################################################################
# latestFinal optaplanner links
###############################################################################

if [[ "${kieVersion}" == *Final* ]]; then
    rm latestFinal
    ln -s ${kieVersion} latestFinal
    echo "Uploading Final links for optaplanner..."
    rsync -a --protocol=28 latestFinal $urlBase_optaplanner/downloads_htdocs/optaplanner/release/
    rsync -a --protocol=28 latestFinal $urlBase_optaplanner/docs_htdocs/optaplanner/release/
fi

urlBase_jbpm="jbpm@filemgmt.jboss.org:"

###############################################################################
# latest jbpm links
###############################################################################
touch ${kieVersion}
rm latest
ln -s ${kieVersion} latest

echo "Uploading normal links for jbpm..."
rsync -a --protocol=28 latest $urlBase_jbpm/downloads_htdocs/jbpm/release/
rsync -a --protocol=28 latest $urlBase_jbpm/docs_htdocs/jbpm/release/


###############################################################################
# latestFinal jbpm links
###############################################################################

if [[ "${kieVersion}" == *Final* ]]; then
    rm latestFinal
    ln -s ${kieVersion} latestFinal
    echo "Uploading Final links for jbpm..."
    rsync -a --protocol=28 latestFinal $urlBase_jbpm/downloads_htdocs/jbpm/release/
    rsync -a --protocol=28 latestFinal $urlBase_jbpm/docs_htdocs/jbpm/release/
fi

###############################################################################
# remove filemgmt_links directory
###############################################################################
cd ..
rm -rf filemgmt_links
