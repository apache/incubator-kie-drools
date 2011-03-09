#!/bin/sh

mainJar=lib/drools-planner-examples-${project.version}.jar
mainClass=org.drools.planner.examples.nurserostering.competition.NurseRosteringCompetitionSprintApp

echo "Usage: ./nurseRosteringSprint.sh [timeInSeconds]"
echo "For example: ./nurseRosteringSprint.sh"
echo "             ./nurseRosteringSprint.sh 10"
echo "All files under the input directory will be solved and placed under the output directory"
echo ""
echo "Some notes:"
echo "- Working dir should be the directory of this script."
echo "- Java must be the Sun JDK, at least version 6 update 10 (preferably the last update)"
echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
echo "  For example: export JAVA_HOME=/usr/lib/jvm/java-6-sun"
echo
echo "Starting competition app..."

# -Xmx128M probably works too, but it might be slower
$JAVA_HOME/bin/java -Xms256m -Xmx1024m -server -cp ${mainJar} ${mainClass} $*
