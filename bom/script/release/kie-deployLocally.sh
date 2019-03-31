#!/bin/bash -e
# removing KIE artifacts from local maven repo (basically all possible SNAPSHOTs)
echo $MAVEN_REPO_LOCAL "="

if [ -d $MAVEN_REPO_LOCAL ]; then
    rm -rf $MAVEN_REPO_LOCAL/org/jboss/dashboard-builder/
    rm -rf $MAVEN_REPO_LOCAL/org/kie/
    rm -rf $MAVEN_REPO_LOCAL/org/drools/
    rm -rf $MAVEN_REPO_LOCAL/org/jbpm/
    rm -rf $MAVEN_REPO_LOCAL/org/optaplanner/
    rm -rf $MAVEN_REPO_LOCAL/org/uberfire/
    
fi

# clone the build-bootstrap that contains the other build scripts
if [ "$target" == "community" ]; then
   git clone git@github.com:kiegroup/droolsjbpm-build-bootstrap.git --branch $releaseBranch
else
   git clone ssh://jb-ip-tooling-jenkins@code.engineering.redhat.com/kiegroup/droolsjbpm-build-bootstrap --branch $releaseBranch
fi

# clone rest of the repos and checkout to this branch
./droolsjbpm-build-bootstrap/script/git-clone-others.sh --branch $releaseBranch --depth 100

# removes kie.properties if it exists
file="kie.properties"
if [ -f "$file" ]; then
   echo "$file found."
   rm $file
fi

# fetch the <version.org.kie> from kie-parent-metadata pom.xml and set it on parameter KIE_VERSION
kieVersion=$(sed -e 's/^[ \t]*//' -e 's/[ \t]*$//' -n -e 's/<version.org.kie>\(.*\)<\/version.org.kie>/\1/p' droolsjbpm-build-bootstrap/pom.xml)

# creates a properties file to pass variables and moves it to the root directory
echo kieVersion=$kieVersion > kie.properties
echo target=$target >> kie.properties

# build release branches
if [ "$target" == "community" ]; then
   deployDir=$WORKSPACE/community-deploy-dir
   # (1) do a full build, but deploy only into local dir
   # we will deploy into remote staging repo only once the whole build passed (to save time and bandwith)   
   ./droolsjbpm-build-bootstrap/script/mvn-all.sh -B -e -U clean deploy -s $SETTINGS_XML_FILE -Dkie.maven.settings.custom=$SETTINGS_XML_FILE -Dfull -Drelease -DaltDeploymentRepository=local::default::file://$deployDir -Dmaven.test.failure.ignore=true -Dgwt.memory.settings="-Xmx10g"
  
else
   deployDir=$WORKSPACE/prod-deploy-dir
   # (1) do a full build with prod look & feel (-Dproductized), but deploy only into local dir
   # we will deploy into remote staging repo only once the whole build passed (to save time and bandwith)   
   ./droolsjbpm-build-bootstrap/script/mvn-all.sh -B -e -U clean deploy -s $SETTINGS_XML_FILE -Dkie.maven.settings.custom=$SETTINGS_XML_FILE -Dfull -Dproductized -Drelease -DaltDeploymentRepository=local::default::file://$deployDir -Dmaven.test.failure.ignore=true -Dgwt.memory.settings="-Xmx10g"

fi
