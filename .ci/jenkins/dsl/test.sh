#!/bin/bash -e

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)

export DSL_DEFAULT_MAIN_CONFIG_FILE_REPO=kiegroup/optaplanner
export DSL_DEFAULT_MAIN_CONFIG_FILE_REF=main
export DSL_DEFAULT_MAIN_CONFIG_FILE_PATH=.ci/jenkins/config/main.yaml

file=$(mktemp)
# For more usage of the script, use ./test.sh -h
curl -o ${file} https://raw.githubusercontent.com/kiegroup/kogito-pipelines/main/dsl/seed/scripts/seed_test.sh
chmod u+x ${file}
${file} $@