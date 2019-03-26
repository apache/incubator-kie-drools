#!/bin/bash -e

# fetch the <version.org.kie> from kie-parent-metadata pom.xml and set it on parameter KIE_VERSION
kieVersion=$(sed -e 's/^[ \t]*//' -e 's/[ \t]*$//' -n -e 's/<version.org.kie>\(.*\)<\/version.org.kie>/\1/p' droolsjbpm-build-bootstrap/pom.xml)

if [ "$target" == "community" ]; then
   stagingRep=15c58a1abc895b
   deployDir=$WORKSPACE/community-deploy-dir
else
   stagingRep=15c3321d12936e
   deployDir=$WORKSPACE/prod-deploy-dir
fi

cd $deployDir
# upload the content to remote staging repo
mvn -B -e org.sonatype.plugins:nexus-staging-maven-plugin:1.6.5:deploy-staged-repository -DnexusUrl=https://repository.jboss.org/nexus -DserverId=jboss-releases-repository\
 -DrepositoryDirectory=$deployDir -DstagingProfileId=$stagingRep -DstagingDescription="kie-$kieVersion" -DstagingProgressTimeoutMinutes=30

