#!/bin/sh

# Using the same package in multiple modules (jars/wars) is bad for OSGi and Jigsaw.
# This script detects such split packages.
# TODO it outputs false positives (but no false negatives, so it's reliable):
# grouping packages that have no direct classes, such as org and org.drools and org.optaplanner


scriptDir=$(dirname $0)
projectDir=${scriptDir}/../../..

echo "Split packages in src/main/java:"
echo "================================"
ls -R ${projectDir} | grep "src/main/java/" | grep -v "target" | sed "s/\(.*\/src\/main\/java\/\)\(.*\)\:/\2/g" | sort | uniq -c | grep -v "\s*1 "
echo ""

echo "Split packages in src/test/java:"
echo "================================"
ls -R ${projectDir} | grep "src/test/java/" | grep -v "target" | sed "s/\(.*\/src\/test\/java\/\)\(.*\)\:/\2/g" | sort | uniq -c | grep -v "\s*1 "
echo ""
