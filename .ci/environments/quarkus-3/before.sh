#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
ci="${CI:-false}"

quarkus_version=${QUARKUS_VERSION:-3.0.0.Alpha5}
quarkus_file="${script_dir_path}/quarkus3.yml"
project_version='quarkus-3-SNAPSHOT'

echo "Update project with Quarkus version ${quarkus_version}"

set -x

# Update recipe file with correct quarkus 3 version
sed -i "s|{QUARKUS_VERSION}|${quarkus_version}|g" "${quarkus_file}"

# In CI we need the main branch snapshot artifacts deployed locally
if [ "${ci}" = "true" ]; then
    ${mvn_cmd} clean install -Dquickly
fi

# Use a different version to not enter in conflict locally with other artifacts
${mvn_cmd} -e -N -Dfull -DnewVersion=${project_version} -DallowSnapshots=true -DgenerateBackupPoms=false versions:set
${mvn_cmd} -e -Dproperty=version.org.kie -DnewVersion=${project_version} -DallowSnapshots=true -DgenerateBackupPoms=false versions:set-property

# Make sure artifacts are updated locally
${mvn_cmd} clean install -Dquickly

# Launch Quarkus 3 Openrewrite
${mvn_cmd} org.openrewrite.maven:rewrite-maven-plugin:4.36.0:run \
    -Drewrite.configLocation="${quarkus_file}" \
    -DactiveRecipes=io.quarkus.openrewrite.Quarkus3 \
    -Denforcer.skip \
    -fae \
    -Dexclusions=**/target \
    -DplainTextMasks=**/kmodule.xml

# Update dependencies with Quarkus 3 bom
${mvn_cmd} \
    -pl :kogito-dependencies-bom \
    -pl :kogito-build-parent \
    -pl :kogito-quarkus-bom \
    -pl :kogito-build-no-bom-parent \
    -DremotePom=io.quarkus:quarkus-bom:${quarkus_version} \
    -DupdatePropertyVersions=true \
    -DupdateDependencies=true \
    -DgenerateBackupPoms=false \
    versions:compare-dependencies
