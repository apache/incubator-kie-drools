#!/bin/bash

#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

version_upgrade=true
commit=true

while [ -n "$1" ]
do
  case "$1" in
  --no-version-upgrade) version_upgrade=false ;;
  --no-commit) commit=false ;;
  esac
  shift
done

script_dir_path=$(cd "$(dirname "${BASH_SOURCE[0]}")" || exit; pwd -P)

mvn_cmd="mvn -B ${BUILD_MVN_OPTS:-}"
optaplanner_file="$script_dir_path/optaplanner-quarkus3.yaml"
optaplanner_root="$script_dir_path/../.."

# Install artifacts locally.
${mvn_cmd} clean install -Dquickly

# Apply scripts
for script_file in "$script_dir_path"/scripts/*
do
  source "$script_file" "$optaplanner_root" || exit;
done

project_version=$(${mvn_cmd} help:evaluate -Dexpression=project.version -q -DforceStdout)

# Run the recipe.
export MAVEN_OPTS="-Xmx4g"
${mvn_cmd} rewrite:run \
  -Drewrite.configLocation="${optaplanner_file}" \
  -Drewrite.recipeArtifactCoordinates=org.optaplanner:optaplanner-migration:"$project_version" \
  -Drewrite.exclusions=optaplanner-examples/data/**,optaplanner-migration/** \
  -Drewrite.activeRecipes=org.optaplanner.openrewrite.Quarkus3 \
  -Dfull \
  -Dquickly \
  -Dmigration \

unset MAVEN_OPTS
# Remove obsolete spring.factories
find "${script_dir_path}/../../optaplanner-spring-integration" -type f -name "spring.factories" -exec rm {} \;

if [[ "$version_upgrade" == "true" ]]; then
  # The formatter and impsort goals override validation activated by the CI environment variable.
  ${mvn_cmd} process-test-sources -Dformatter.goal=format -Dimpsort.goal=sort -Denforcer.skip

  # 8.x.y(-SNAPSHOT|.Final) -> 9.x.y(-SNAPSHOT|.Final)
  new_project_version="9${project_version:1}"
  ${mvn_cmd} versions:set \
    -Dfull \
    -DallowSnapshots=true \
    -DgenerateBackupPoms=false \
    -DnewVersion="${new_project_version}"
fi

if [[ "$commit" == "true" ]]; then
  # Commit the changes.
  git status
  git add -u
  git commit -m "Migrate to OptaPlanner 9"
fi