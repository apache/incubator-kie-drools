#!/bin/bash

set -e

whereAmI=$(pwd)
echo $wherAmI

droolsDocs=drools@filemgmt.jboss.org:/docs_htdocs/drools/release
droolsHtdocs=drools@filemgmt.jboss.org:/downloads_htdocs/drools/release
jbpmDocs=jbpm@filemgmt.jboss.org:/docs_htdocs/jbpm/release
jbpmHtdocs=jbpm@filemgmt.jboss.org:/downloads_htdocs/jbpm/release
jbpmServiceRepo=jbpm@filemgmt.jboss.org:/downloads_htdocs/jbpm/release
optaplannerDocs=optaplanner@filemgmt.jboss.org:/docs_htdocs/optaplanner/release
optaplannerHtdocs=optaplanner@filemgmt.jboss.org:/downloads_htdocs/optaplanner/release


# create directory on filemgmt.jboss.org for new release
touch upload_version
echo "mkdir" $version > upload_version
chmod +x upload_version


sftp -b upload_version $droolsDocs
sftp -b upload_version $droolsHtdocs
sftp -b upload_version $jbpmDocs
sftp -b upload_version $jbpmHtdocs
sftp -b upload_version $optaplannerDocs
sftp -b upload_version $optaplannerHtdocs

#creates directories for updatesite for drools and jbpm on filemgmt.jboss.org
touch upload_drools
echo "mkdir org.drools.updatesite" > upload_drools
chmod +x upload_drools
sftp -b upload_drools $droolsHtdocs/$version

touch upload_jbpm
echo "mkdir updatesite" > upload_jbpm
chmod +x upload_jbpm
sftp -b upload_jbpm $jbpmHtdocs/$version


#creates directories for docs for drools and jbpm and optaplanner on filemgmt.jboss.org
touch upload_drools_docs
echo "mkdir drools-docs" > upload_drools_docs
chmod +x upload_drools_docs
sftp -b upload_drools_docs $droolsDocs/$version/

touch upload_kie_api_javadoc
echo "mkdir kie-api-javadoc" > upload_kie_api_javadoc
chmod +x upload_kie_api_javadoc
sftp -b upload_kie_api_javadoc $droolsDocs/$version

touch upload_jbpm_docs
echo "mkdir jbpm-docs" > upload_jbpm_docs
chmod +x upload_jbpm_docs
sftp -b upload_jbpm_docs $jbpmDocs/$version

touch upload_optaplanner_docs
echo "mkdir optaplanner-docs" > upload_optaplanner_docs
chmod +x upload_optaplanner_docs
sftp -b upload_optaplanner_docs $optaplannerDocs/$version

touch upload_optaplanner_javadoc
echo "mkdir optaplanner-javadoc" > upload_optaplanner_javadoc
chmod +x upload_optaplanner_javadoc
sftp -b upload_optaplanner_javadoc $optaplannerDocs/$version

touch upload_optaplanner_wb_es_docs
echo "mkdir optaplanner-wb-es-docs" > upload_optaplanner_wb_es_docs
chmod +x upload_optaplanner_wb_es_docs
sftp -b upload_optaplanner_wb_es_docs $optaplannerDocs/$version

touch upload_service_repository
echo "mkdir service-repository" > upload_service_repository
chmod +x upload_service_repository
sftp -b upload_service_repository $jbpmServiceRepo/$version

# copies drools binaries to filemgmt.jboss.org
scp -r droolsjbpm-tools/droolsjbpm-tools-distribution/target/droolsjbpm-tools-distribution-$version/droolsjbpm-tools-distribution-$version/binaries/org.drools.updatesite/* $droolsHtdocs/$version/org.drools.updatesite
scp drools/drools-distribution/target/drools-distribution-$version.zip $droolsHtdocs/$version
scp droolsjbpm-integration/droolsjbpm-integration-distribution/target/droolsjbpm-integration-distribution-$version.zip $droolsHtdocs/$version
scp droolsjbpm-tools/droolsjbpm-tools-distribution/target/droolsjbpm-tools-distribution-$version.zip $droolsHtdocs/$version
scp kie-wb-distributions/business-central-parent/business-central-distribution-wars/business-central/target/business-central-$version-*.war $droolsHtdocs/$version
scp droolsjbpm-integration/kie-server-parent/kie-server-wars/kie-server-distribution/target/kie-server-distribution-$version.zip $droolsHtdocs/$version

#copies drools-docs and kie-api-javadoc to filemgmt.jboss.or
scp -r kie-docs/doc-content/drools-docs/target/generated-docs/* $droolsDocs/$version/drools-docs
scp -r droolsjbpm-knowledge/kie-api/target/apidocs/* $droolsDocs/$version/kie-api-javadoc

#copies jbpm binaries to filemgmt.jboss.org
scp -r droolsjbpm-tools/droolsjbpm-tools-distribution/target/droolsjbpm-tools-distribution-$version/droolsjbpm-tools-distribution-$version/binaries/org.drools.updatesite/* $jbpmHtdocs/$version/updatesite
scp jbpm/jbpm-distribution/target/jbpm-$version-bin.zip $jbpmHtdocs/$version
scp jbpm/jbpm-distribution/target/jbpm-$version-examples.zip $jbpmHtdocs/$version
scp kie-wb-distributions/business-central-parent/jbpm-server-distribution/target/jbpm-server-$version-dist.zip $jbpmHtdocs/$version

#copies the jbpm-installers to filemgmt.jboss.org
jbpmHtdocs=jbpm@filemgmt.jboss.org:/downloads_htdocs/jbpm/release

uploadInstaller(){
        # upload installers to filemgmt.jboss.org
        scp jbpm-installer-$version.zip $jbpmHtdocs/$version
}

uploadAllInstaller(){
        # upload installers to filemgmt.jboss.org
        scp jbpm-installer-$version.zip $jbpmHtdocs/$version
        # upload installers to filemgmt.jboss.org
        scp jbpm-installer-full-$version.zip $jbpmHtdocs/$version
}

if [[ $version == *"Final"* ]] ;then
        uploadAllInstaller
else
        uploadInstaller
fi

# copies jbpm work items into service repository
scp -r jbpm-work-items/repository/target/repository-$version/* $jbpmServiceRepo/$version/service-repository

#copies jbpm-docs to filemgmt.jboss.org
scp -r kie-docs/doc-content/jbpm-docs/target/generated-docs/* $jbpmDocs/$version/jbpm-docs

#copies optaplanner binaries to filemgmt.jboss.org
scp optaplanner/optaplanner-distribution/target/optaplanner-distribution-$version.zip $optaplannerHtdocs/$version

#copies optaplanner-docs and optaplanner-javadoc to filemgmt.jboss.org
scp -r optaplanner/optaplanner-docs/target/generated-docs/* $optaplannerDocs/$version/optaplanner-docs
scp -r optaplanner/optaplanner-distribution/target/optaplanner-distribution-$version/optaplanner-distribution-$version/javadocs/* $optaplannerDocs/$version/optaplanner-javadoc
scp -r kie-docs/doc-content/optaplanner-wb-es-docs/target/generated-docs/* $optaplannerDocs/$version/optaplanner-wb-es-docs

# clean upload files
rm upload_*

# runs create_filemgmt_links.sh
sh droolsjbpm-build-bootstrap/script/release/create_filemgmt_links.sh $version
