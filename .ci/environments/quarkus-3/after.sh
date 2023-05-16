#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"

# Retrieve current Maven project version
project_version=$(mvn -q -Dexpression=project.version -DforceStdout help:evaluate)
# New version is based on current project version and increment the Major => (M+1).m.y
new_version=$(echo ${project_version} | awk -F. -v OFS=. '{$1 += 1 ; print}')

${mvn_cmd} -fae -N -e versions:update-parent -Dfull -DparentVersion="[${new_version}]" -DallowSnapshots=true -DgenerateBackupPoms=false
${mvn_cmd} -fae -N -e versions:update-child-modules -Dfull -DallowSnapshots=true -DgenerateBackupPoms=false