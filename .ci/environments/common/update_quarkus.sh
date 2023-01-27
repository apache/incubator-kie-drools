#!/bin/bash
set -euo pipefail

mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
quarkus_version="${QUARKUS_VERSION}"

echo "Update project with Quarkus version ${QUARKUS_VERSION}"

echo "Nothing to be done on this project ..."
