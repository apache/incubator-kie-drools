#!/bin/bash -e

file=$(mktemp)
# For more usage of the script, use ./test.sh -h
curl -o ${file} https://raw.githubusercontent.com/kiegroup/kogito-pipelines/main/dsl/seed/scripts/seed_test.sh
chmod u+x ${file}
${file} $@