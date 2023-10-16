#!/usr/bin/env bash
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


trap finish SIGTERM EXIT

set -o errexit
set -o nounset
#set -o xtrace


#######################################
# Clear popd stack.
#
# Globals:
#   None
# Arguments:
#   None
#######################################
finish() {
    popd
}

#######################################
# Run docker images. Based on docker-compose.yml
#
# Globals:
#   None
# Arguments:
#   None
#######################################

pushd .

if [[ "${PWD}" == */docker-compose ]]
then 
    echo 'Application starts from the docker-compose folder - probably manual start of this script'
elif [[ "${PWD}" == */packages/trusty ]]
then 
    echo 'Application starts from the trusty folder - probably run some pnpm script'
    echo 'Move to the docker-compose folder'
    cd cypress/docker-compose
else
    >&2 echo "error: script starts from unexpected location: ${PWD}"
    >&2 echo "error: script expects /ui-packages/packages/trusty or /ui-packages/packages/trusty/cypress/docker-compose folders"
    exit
fi

docker-compose up

