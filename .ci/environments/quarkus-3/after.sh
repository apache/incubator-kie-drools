#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"

project_version=$(${mvn_cmd} -q -Dexpression=project.version -DforceStdout help:evaluate)
new_version=$(echo ${project_version} | awk -F. -v OFS=. '{$1 += 1 ; print}')

# Change version
${mvn_cmd} -e -N -Dfull -DnewVersion=${new_version} -DallowSnapshots=true -DgenerateBackupPoms=false versions:set