#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
ci="${CI:-false}"

quarkus_version=${QUARKUS_VERSION:-3.0.0.Alpha3}
quarkus_file="${script_dir_path}/quarkus3.yml"
project_version='quarkus-3-SNAPSHOT'

echo "Update project with Quarkus version ${quarkus_version}"

set -x

sed -i "s|{QUARKUS_VERSION}|${quarkus_version}|g" "${quarkus_file}"

if [ "${ci}" = "true" ]; then
    # In CI we need the main branch snapshot artifacts deployed locally
    ${mvn_cmd} clean install -Dquickly
fi

# Change version
${mvn_cmd} -e -N -Dfull -DnewVersion=${project_version} -DallowSnapshots=true -DgenerateBackupPoms=false versions:set

# Make sure artifacts are updated locally
${mvn_cmd} clean install -Dquickly

# Launch Quarkus 3 Openrewrite
${mvn_cmd} org.openrewrite.maven:rewrite-maven-plugin:4.41.1:run \
    -Drewrite.configLocation="${quarkus_file}" \
    -DactiveRecipes=io.quarkus.openrewrite.Quarkus3 \
    -Drewrite.recipeArtifactCoordinates=org.kie:jpmml-migration-recipe:"${project_version}" \
    -Denforcer.skip \
    -fae \
    -Dexclusions=**/target \
    -DplainTextMasks=**/kmodule.xml

# Update dependencies with Quarkus 3 bom
${mvn_cmd} versions:compare-dependencies -pl :drools-build-parent -DremotePom=io.quarkus:quarkus-bom:${quarkus_version} -DupdatePropertyVersions=true -DupdateDependencies=true -DgenerateBackupPoms=false
