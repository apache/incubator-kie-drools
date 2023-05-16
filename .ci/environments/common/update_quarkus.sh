#!/bin/bash
set -euo pipefail

mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"

source <(curl -s https://raw.githubusercontent.com/kiegroup/kogito-pipelines/main/dsl/seed/scripts/install_quarkus.sh)

echo "Update project with Quarkus version ${QUARKUS_VERSION}"

# Update with Quarkus version and commit
${mvn_cmd} \
    -pl :kogito-apps-build-parent \
    -DremotePom=io.quarkus:quarkus-bom:${QUARKUS_VERSION} \
    -DupdatePropertyVersions=true \
    -DupdateDependencies=true \
    -DgenerateBackupPoms=false \
    versions:compare-dependencies

