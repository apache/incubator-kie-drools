#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
project_version='quarkus-3-SNAPSHOT'

# Change version
${mvn_cmd} -e -N -Dfull -DnewVersion=${project_version} -DallowSnapshots=true -DgenerateBackupPoms=false versions:set