#!/bin/sh

# Change directory to the directory of the script
cd `dirname $0`

mainJar=binaries/drools-examples-${project.version}.jar
mainClass=org.drools.examples.DroolsExamplesApp

echo "Usage: ./runExamples.sh"
echo "For example: ./runExamples.sh"
echo "Some notes:"
echo "- Working dir should be the directory of this script."
echo "- Java is recommended to be JDK and java 6 for optimal performance"
echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
echo "  For example (linux): export JAVA_HOME=/usr/lib/jvm/java-6-sun"
echo "  For example (mac): export JAVA_HOME=/Library/Java/Home"
echo
echo "Starting examples app..."

# You can use -Xmx128m or less too, but it might be slower
# You can remove -server to run it on a JRE without a JDK, but it will be slower
$JAVA_HOME/bin/java -Xms256m -Xmx512m -server -cp ${mainJar} ${mainClass} $*
