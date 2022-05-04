#!/bin/bash -e

file=$(mktemp)
# For more usage of the script, use ./test.sh -h
# TODO to update before merge
curl -o ${file} https://raw.githubusercontent.com/radtriste/kogito-pipelines/kogito-6962/dsl/seed/scripts/seed_test.sh
chmod u+x ${file}
${file} $@