#!/bin/bash -e

echo "kieVersion:" $kieVersion
echo "target" : $target

if [ "$target" == "community" ]; then 
   stagingRep=kie-group
else
   stagingRep=kie-internal-group
fi
pwd
echo "WORKSPACE :" $WORKSPACE
# wget the tar.gz sources
wget -q https://repository.jboss.org/nexus/content/groups/$stagingRep/org/drools/droolsjbpm-integration/$kieVersion/droolsjbpm-integration-$kieVersion-project-sources.tar.gz -O sources.tar.gz

tar xzf sources.tar.gz
mv droolsjbpm-integration-$kieVersion/* .
rmdir droolsjbpm-integration-$kieVersion
