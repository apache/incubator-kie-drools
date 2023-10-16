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

set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"

# Retrieve current Maven project version
project_version=$(mvn -q -Dexpression=project.version -DforceStdout help:evaluate)
# New version is based on current project version and increment the Major => (M+1).m.y
new_version=$(echo ${project_version} | awk -F. -v OFS=. '{$1 += 1 ; print}')

${mvn_cmd} -fae -N -e versions:update-parent -Dfull -DparentVersion="[${new_version}]" -DallowSnapshots=true -DgenerateBackupPoms=false
${mvn_cmd} -fae -N -e versions:update-child-modules -Dfull -DallowSnapshots=true -DgenerateBackupPoms=false