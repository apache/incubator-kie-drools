#!/bin/bash -e

# this script build the jbpm-installer and jbpm-installer full

if [ "$target" == "public" ]; then
   URLgroup="public-jboss"
else
   URLgroup="kie-group"
fi

createJbpmInstaller(){
        # download and unzip the jbpm-installer-<version>.zip
        wget https://repository.jboss.org/nexus/content/groups/$URLgroup/org/jbpm/jbpm-installer/$version/jbpm-installer-$version.zip
        unzip jbpm-installer*.zip

        # modifications in build.properties
        sed -i -e '/^#/!s/jBPM.version=\${snapshot.version}/jBPM.version=\${release.version}/g' jbpm-installer-$version/build.properties
        sed -i -e '/^#/!s/jBPM.url=http.*/jBPM.url=https:\/\/repository.jboss.org\/nexus\/content\/groups\/'$URLgroup'\/org\/drools\/droolsjbpm-bpms-distribution\/\${jBPM.version}\/droolsjbpm-bpms-distribution-\${jBPM.version}-bin.zip/g' jbpm-installer-$version/build.properties
        sed -i -e '/^#/!s/jBPM.console.url=http.*/jBPM.console.url=https:\/\/repository.jboss.org\/nexus\/content\/groups\/'$URLgroup'\/org\/kie\/business-central\/\${jBPM.version}\/business-central-\${jBPM.version}-wildfly14.war/g' jbpm-installer-$version/build.properties
        sed -i -e '/^#/!s/jBPM.casemgmt.url=https.*/jBPM.casemgmt.url=https:\/\/repository.jboss.org\/nexus\/content\/groups\/'$URLgroup'\/org\/jbpm\/jbpm-wb-case-mgmt-showcase\/\${jBPM.version}\/jbpm-wb-case-mgmt-showcase-\${jBPM.version}-wildfly14.war/g' jbpm-installer-$version/build.properties
        sed -i -e '/^#/!s/kie.server.url=http.*/kie.server.url=http:\/\/repository.jboss.org\/nexus\/content\/groups\/'$URLgroup'\/org\/kie\/server\/kie-server\/${jBPM.version}\/kie-server-\${jBPM.version}-ee7.war/g' jbpm-installer-$version/build.properties
        sed -i -e '/^#/!s/droolsjbpm.eclipse.version=\${snapshot.version}/droolsjbpm.eclipse.version=\${release.version}/g' jbpm-installer-$version/build.properties
        sed -i -e '/^#/!s/droolsjbpm.eclipse.url=http.*/droolsjbpm.eclipse.url=https:\/\/repository.jboss.org\/nexus\/content\/groups\/'$URLgroup'\/org\/drools\/org.drools.updatesite\/\${droolsjbpm.eclipse.version}\/org.drools.updatesite-\${droolsjbpm.eclipse.version}.zip/g' jbpm-installer-$version/build.properties

        # add modified build.properties to jbpm-installer-<version>.zip
        zip -ur jbpm-installer-$version.zip jbpm-installer-$version/build.properties
}

createJbpmFullInstaller(){

        # downloads installer and modifies build.properties
        createJbpmInstaller
        # moves installer.zip to installer-full-<version>
        cp jbpm-installer-$version.zip jbpm-installer-full-$version.zip

        # creates content for jbpm-installer-full-<version>.zip
        cd jbpm-installer-$version
        ant install.demo
        cd ..
        zip -ur jbpm-installer-full-$version.zip jbpm-installer-$version/db/driver/
        zip -ur jbpm-installer-full-$version.zip jbpm-installer-$version/lib/
        zip -d jbpm-installer-full-$version.zip "jbpm-installer-$version/lib/eclipse-java-neon-2*"
}

if [[ $version == *"Final"* ]] ;then
        createJbpmFullInstaller
else
        createJbpmInstaller
fi

