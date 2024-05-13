#!/bin/bash -e

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

# Used to retrieve current git author, to set correctly the main config file repo
GIT_SERVER='github.com'

git_url=$(git remote -v | grep origin | awk -F' ' '{print $2}' | head -n 1)
if [ -z "${git_url}" ]; then
  echo "Script must be executed in a Git repository for this script to run correctly"
  exit 1
fi
echo "git_url = ${git_url}"

git_server_url=
if [[ "${git_url}" = https://* ]]; then
  git_server_url="https://${GIT_SERVER}/"
elif [[ "${git_url}" = git@* ]]; then 
  git_server_url="git@${GIT_SERVER}:"
else
  echo "Unknown protocol for url ${git_url}"
  exit 1
fi

git_author="$(echo ${git_url} | awk -F"${git_server_url}" '{print $2}' | awk -F. '{print $1}'  | awk -F/ '{print $1}')"

export DSL_DEFAULT_MAIN_CONFIG_FILE_REPO="${git_author}"/incubator-kie-drools
export DSL_DEFAULT_FALLBACK_MAIN_CONFIG_FILE_REPO=apache/incubator-kie-drools
export DSL_DEFAULT_MAIN_CONFIG_FILE_PATH=.ci/jenkins/config/main.yaml
export DSL_DEFAULT_BRANCH_CONFIG_FILE_REPO="${git_author}"/incubator-kie-drools

file=$(mktemp)
# For more usage of the script, use ./test.sh -h
curl -o ${file} https://raw.githubusercontent.com/apache/incubator-kie-kogito-pipelines/main/dsl/seed/scripts/seed_test.sh
chmod u+x ${file}
${file} $@