#!/bin/sh

# Change directory to the directory of the script
cd `dirname $0`

# Most examples run (potentially slower) with max heap of 128 MB (so -Xmx128m), but 1 example's dataset requires 1.5 GB
jvmOptions="-Xms256m -Xmx1536m -Dorg.optaplanner.examples.dataDir=sources/data/"
mainClass=org.optaplanner.examples.app.OptaPlannerExamplesApp
mainClasspath="binaries/*:../binaries/*"

echo "Usage: ./runExamples.sh"
echo "Notes:"
echo "- Java must be installed. Get the JRE (http://www.java.com) or the JDK."
echo "- For optimal performance, Java is recommended to be OpenJDK 8 or higher."
echo "- For JDK, the environment variable JAVA_HOME should be set to the JDK installation directory"
echo "  For example (linux): export JAVA_HOME=/usr/lib/jvm/java-openjdk"
echo "  For example (mac): export JAVA_HOME=/Library/Java/Home"
echo "- The working dir should be the directory of this script."
echo

if [ -f $JAVA_HOME/bin/java ]; then
    echo "Starting examples app with JDK from environment variable JAVA_HOME ($JAVA_HOME)..."
    $JAVA_HOME/bin/java ${jvmOptions} -server -cp ${mainClasspath} ${mainClass} $*
else
    echo "Starting examples app with java from environment variable PATH..."
    java ${jvmOptions} -cp ${mainClasspath} ${mainClass} $*
fi

if [ $? != 0 ] ; then
    echo
    echo "ERROR: Check if Java is installed and environment variable JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    read -p "Press [Enter] key to close this window." dummyVar
fi
