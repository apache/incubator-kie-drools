#!/bin/bash -e
TEMP_DIR=`mktemp -d`

author=$1
branch=$2

if [ -z $author ]; then
  author='kiegroup'
fi

if [ -z $branch ]; then
  branch='master'
fi

git clone --single-branch --branch $branch https://github.com/${author}/jenkins-pipeline-shared-libraries.git $TEMP_DIR

cd $TEMP_DIR && mvn clean install -DskipTests