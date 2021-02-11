#!/bin/bash -e

TEMP_DIR=`mktemp -d`

branch=$1
author=$2

if [ -z $branch ]; then
  branch='master'
fi

if [ -z $author ]; then
  author='kiegroup'
fi

echo "----- Cloning pipelines repo from ${author} on branch ${branch}"
git clone --single-branch --branch ${branch} https://github.com/${author}/kogito-pipelines.git $TEMP_DIR

echo '----- Launching seed tests'
${TEMP_DIR}/dsl/seed/scripts/seed_test.sh ${TEMP_DIR}
