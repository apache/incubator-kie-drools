#!/usr/bin/env bash

trap finish SIGTERM EXIT

set -o errexit
set -o nounset
#set -o xtrace

#######################################
# Write information about script on stdout
#
# Globals:
#   None
# Arguments:
#   None
#######################################
usage() {
    echo "This script builds docker images based on the current state of this git repository."
    echo "Useage: build.sh [OPTION]"
    echo "Options:"
    echo "-c remove previously build docker images"
    echo "-h show help"
}

#######################################
# Clear docker images. List of deleted images is saved in local array.
# It writes information about state of docker images on stdout to help maintain list of images.
#
# Globals:
#   None
# Arguments:
#   None
#######################################
clean() {
    echo "Clean docker images:"
    local kie_images=(
        'org.kie.kogito/integration-tests-trusty-service-quarkus'
        'org.kie.kogito/trusty-service-infinispan'
        'org.kie.kogito/explainability-service-messaging'
        'org.kie.kogito/trusty-ui'
        'org.kie.kogito/data-index-service-postgresql'
        'org.kie.kogito/data-index-service-mongodb'
        'org.kie.kogito/data-index-service-infinispan'
        'org.kie.kogito/jobs-service-mongodb'
        'org.kie.kogito/jobs-service-postgresql'
        'org.kie.kogito/jobs-service-infinispan'
        'org.kie.kogito/jobs-service-common'
        'org.kie.kogito/integration-tests-trusty-service-springboot'
        'org.kie.kogito/data-index-service-inmemory'
        'org.kie.kogito/jobs-service-inmemory'
    )

    for img in ${kie_images[@]}; do
        local hash_img=$(docker images ${img} --all --quiet)
        if [ ! -z "${hash_img}" ]; then
            echo "Image ${img} was removed"
            docker rmi ${hash_img}
        else
            echo "Image '${img}' has been allready removed"
        fi
    done

    echo "State of 'docker images':"
    docker images
}

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
# Build project to prepare docker images with backend applications.
#
# Globals:
#   None
# Arguments:
#   None
#######################################

pushd .

while getopts "ch" opt; do
    case "${opt}" in
    c)
        clean
        ;;
    h)
        usage
        exit 0
        ;;
    \?)
        echo >&2 "error: Invalid option"
        usage
        exit 1
        ;;
    :) ;;

    *)
        usage
        exit 0
        ;;
    esac
done

kogito_apps=''
if [[ "${PWD}" == */docker-compose ]]; then
    kogito_apps='../../../../..'
    echo 'script starts from the docker-compose folder'
elif [[ "${PWD}" == */packages/trusty ]]; then
    kogito_apps='../../../'
    echo 'script starts from the ui-trusty folder'
else
    echo >&2 "error: script starts from unexpected location: ${PWD}"
    echo >&2 "error: script expects /ui-packages/packages/trusty or /ui-packages/packages/trusty/cypress/docker-compose folders"
    exit 1
fi

cd $(tr --delete '\r' <<<${kogito_apps})
if [[ "${PWD}" == */kogito-apps ]]; then
    #create environment file for docker compose. The content is based on actual project version
    project_version=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
    rm --force ui-packages/packages/trusty/cypress/docker-compose/.env
    echo VERSION=${project_version} >> ui-packages/packages/trusty/cypress/docker-compose/.env

    mvn clean install -DskipTests
else
    echo >&2 "error: script is not in kogito-apps ${kogito_apps}"
    exit 1
fi
