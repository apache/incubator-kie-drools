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

# This script copies the antora-template.yml with asciidoc attributes substituted by a maven build to src/antora.yml
# in optaplanner-docs.

this_script_directory="${BASH_SOURCE%/*}"
if [[ ! -d "$this_script_directory" ]]; then
  this_script_directory="$PWD"
fi

readonly optaplanner_project_root=$this_script_directory/../..
readonly optaplanner_docs=$optaplanner_project_root/optaplanner-docs
readonly antora_yml=$optaplanner_docs/src/antora.yml
readonly antora_yml_template=$optaplanner_docs/target/antora-template.yml
if [ ! -f "$antora_yml_template" ]; then
    echo "The $antora_yml_template with substituted attributes was not found. Maybe build the optaplanner-docs first."
    exit 1
fi

cp "$antora_yml_template" "$antora_yml"


