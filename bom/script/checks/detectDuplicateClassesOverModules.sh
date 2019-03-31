#!/bin/sh

# Using duplicate classes in multiple modules (jars/wars) is bad for OSGi and Jigsaw.
# This script detects such duplicate classes.


scriptDir=$(dirname $0)
projectDir=${scriptDir}/../../..

echo "Duplicate classes in src/main/java or src/main/test:"
echo "===================================================="
find ${projectDir} -name "*.java" | grep "src/.*/java/" | grep -v "target" | sed "s/\(.*\/src\/main\/java\/\)\(.*\)/\2/g" | sed "s/\(.*\/src\/test\/java\/\)\(.*\)/\2/g" | sort | uniq -c | grep -v "\s*1 "
echo ""
