#!/bin/bash -e

echo "kieVersion:" $kieVersion
echo "target" : $target

if [ "$target" == "community" ]; then
   stagingRep=kie-group
else
   stagingRep=kie-internal-group
fi

# wget the tar.gz sources
wget -q https://repository.jboss.org/nexus/content/groups/$stagingRep/org/kie/kie-wb-distributions/$kieVersion/kie-wb-distributions-$kieVersion-project-sources.tar.gz -O sources.tar.gz

tar xzf sources.tar.gz
mv kie-wb-distributions-$kieVersion/* .
rmdir kie-wb-distributions-$kieVersion

