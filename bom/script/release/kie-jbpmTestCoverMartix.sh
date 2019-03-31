#!/bin/bash -e

echo "kieVersion:" $kieVersion
echo "target:" $target

if [ "$target" == "community" ]; then
   stagingRep=kie-group
else
   stagingRep=kie-internal-group
fi

# wget the tar.gz sources
wget -q https://repository.jboss.org/nexus/content/groups/$stagingRep/org/jbpm/jbpm/$kieVersion/jbpm-$kieVersion-project-sources.tar.gz -O sources.tar.gz

tar xzf sources.tar.gz
mv jbpm-$kieVersion/* .
rmdir jbpm-$kieVersion
