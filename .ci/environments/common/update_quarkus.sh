#!/bin/bash
set -euo pipefail

mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"

source <(curl -s https://raw.githubusercontent.com/kiegroup/kogito-pipelines/main/dsl/seed/scripts/install_quarkus.sh)

echo "Update project with Quarkus version ${QUARKUS_VERSION}"

set -x

# Update with Quarkus version and commit
${mvn_cmd} versions:compare-dependencies -pl :drools-build-parent -DremotePom=io.quarkus:quarkus-bom:${QUARKUS_VERSION} -DupdatePropertyVersions=true -DupdateDependencies=true -DgenerateBackupPoms=false
${mvn_cmd} versions:set-property -pl :drools-build-parent -Dproperty=version.io.quarkus -DnewVersion=${QUARKUS_VERSION} -DgenerateBackupPoms=false -Dmaven.wagon.http.ssl.insecure=true
