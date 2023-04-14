#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
ci="${CI:-false}"

rewrite_plugin_version=4.43.0

quarkus_version=${QUARKUS_VERSION:-3.0.0.CR1}
quarkus_file="${script_dir_path}/quarkus3.yml"
project_version='quarkus-3-SNAPSHOT'

rewrite=${1:-'none'}
echo "rewrite "${rewrite}

if [ "rewrite" != ${rewrite} ]; then
    echo "No rewrite to be done. Exited"
    exit 0
fi

export MAVEN_OPTS="-Xmx16192m"

echo "Update project with Quarkus version ${quarkus_version}"

set -x

if [ "${ci}" = "true" ]; then
    # In CI we need the main branch snapshot artifacts deployed locally
    ${mvn_cmd} clean install -Dquickly
fi

# Change version
${mvn_cmd} -e -N -Dfull -DnewVersion=${project_version} -DallowSnapshots=true -DgenerateBackupPoms=false versions:set

# Make sure artifacts are updated locally
${mvn_cmd} clean install -Dquickly

# Launch Quarkus 3 Openrewrite
${mvn_cmd} org.openrewrite.maven:rewrite-maven-plugin:${rewrite_plugin_version}:run \
    -Drewrite.configLocation="${quarkus_file}" \
    -DactiveRecipes=io.quarkus.openrewrite.Quarkus \
    -Drewrite.recipeArtifactCoordinates=org.kie:jpmml-migration-recipe:"${project_version}" \
    -Denforcer.skip \
    -fae \
    -Dexclusions=**/target \
    -DplainTextMasks=**/kmodule.xml

# Update dependencies with Quarkus 3 bom
${mvn_cmd} versions:compare-dependencies -pl :drools-build-parent -DremotePom=io.quarkus:quarkus-bom:${quarkus_version} -DupdatePropertyVersions=true -DupdateDependencies=true -DgenerateBackupPoms=false

# Create the `patches/0001_before_sh.patch` file
git add .
git diff --cached > "${script_dir_path}"/patches/0001_before_sh.patch
git reset

# Commit the change on patch
git add "${script_dir_path}"/patches/0001_before_sh.patch
git commit -m '[Quarkus 3 migration] Updated `before.sh` patch file'

# Reset all other changes as they will be applied next by the `patches/0001_before_sh.patch` file
git reset --hard