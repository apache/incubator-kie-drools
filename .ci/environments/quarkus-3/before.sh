#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"

quarkus_version=${QUARKUS_VERSION:-3.0.0.Alpha3}
quarkus_file="${script_dir_path}/quarkus3.yml"

mavenLocalOldRepo=${MAVEN_LOCAL_OLD_ARTIFACTS_REPO:-'/tmp/kogito/quarkus-3/maven'}

set +e
echo "Retrieve project version"
project_version=$(${mvn_cmd} help:evaluate -Dexpression=project.version -q -DforceStdout)
if [ "$?" != '0' ]; then
    echo "Cannot retrieve project version"
    exit 1
fi
set -e

echo "Update project with Quarkus version ${quarkus_version}"

set -x

# Make sure artifacts are updated locally
${mvn_cmd} clean install \
    -pl jpmml-migration-recipe \
    -Dquickly \
    -Dmaven.repo.local=${mavenLocalOldRepo}

# Update Quarkus version in project
${mvn_cmd} versions:set-property \
    -pl :drools-build-parent \
    -Dproperty=version.io.quarkus \
    -DnewVersion=${quarkus_version} \
    -DgenerateBackupPoms=false \
    -Dmaven.wagon.http.ssl.insecure=true

# Launch Quarkus 3 Openrewrite
${mvn_cmd} org.openrewrite.maven:rewrite-maven-plugin:4.41.1:run \
    -Drewrite.configLocation="${quarkus_file}" \
    -DactiveRecipes=io.quarkus.openrewrite.Quarkus3 \
    -Drewrite.recipeArtifactCoordinates=org.kie:jpmml-migration-recipe:"${project_version}" \
    -Denforcer.skip \
    -Dmaven.repo.local=${mavenLocalOldRepo} \
    -Dexclusions=**/target \
    -DplainTextMasks=**/kmodule.xml

# Update dependencies with Quarkus 3 bom
${mvn_cmd} versions:compare-dependencies -pl :drools-build-parent -DremotePom=io.quarkus:quarkus-bom:${quarkus_version} -DupdatePropertyVersions=true -DupdateDependencies=true -DgenerateBackupPoms=false
