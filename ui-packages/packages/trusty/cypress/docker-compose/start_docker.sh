#!/usr/bin/env bash

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

