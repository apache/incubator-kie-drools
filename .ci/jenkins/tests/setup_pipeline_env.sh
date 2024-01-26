#!/bin/bash -e
TEMP_DIR=`mktemp -d`

author=$1
branch=$2

if [ -z $author ]; then
  author='apache'
fi

if [ -z $branch ]; then
  branch='main'
fi

git clone --single-branch --branch $branch https://github.com/${author}/incubator-kie-kogito-pipelines.git $TEMP_DIR

cd $TEMP_DIR/jenkins-pipeline-shared-libraries && mvn clean install -DskipTests