#!/bin/bash
set -euo pipefail

mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
quarkus_version="${QUARKUS_VERSION}"

echo "Update project with Quarkus version ${QUARKUS_VERSION}"

set -x

# Update with Quarkus version and commit
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

${mvn_cmd} \
    -pl :kogito-dependencies-bom \
    -pl :kogito-build-parent \
    -pl :kogito-quarkus-bom \
    -pl :kogito-build-no-bom-parent \
    -Dproperty=version.io.quarkus \
    -DnewVersion=${quarkus_version} \
    -DgenerateBackupPoms=false \
    -Dmaven.wagon.http.ssl.insecure=true \
    versions:set-property
