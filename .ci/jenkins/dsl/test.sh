#!/bin/bash -e

export DSL_DEFAULT_MAIN_CONFIG_FILE_REPO=kiegroup/kogito-pipelines
export DSL_DEFAULT_MAIN_CONFIG_FILE_REF=main
export DSL_DEFAULT_MAIN_CONFIG_FILE_PATH=dsl/config/main.yaml

file=$(mktemp)
# For more usage of the script, use ./test.sh -h
curl -o ${file} https://raw.githubusercontent.com/kiegroup/kogito-pipelines/main/dsl/seed/scripts/seed_test.sh
chmod u+x ${file}
${file} $@