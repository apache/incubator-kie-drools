#!/bin/sh

# Change directory to the directory of the script
cd "$(dirname $0)" || exit

# Most examples run (potentially slower) with max heap of 128 MB (so -Xmx128m), but 1 example's dataset requires 2 GB
jvmOptions="-Xms256m -Xmx2g -Dorg.optaplanner.examples.dataDir=examples/sources/data/"
mainClass=org.optaplanner.examples.app.OptaPlannerExamplesApp
mainClasspath="examples/binaries/*"

echo "Usage: ./runExamples.sh"
echo "Notes:"
echo "- Java 11 or higher must be installed. Get the latest OpenJDK from (https://adoptium.net/)."
echo "- For JDK, the environment variable JAVA_HOME should be set to the JDK installation directory"
echo "  For example (Linux): export JAVA_HOME=/usr/lib/jvm/java-openjdk"
echo "  For example (macOS): export JAVA_HOME=/Library/Java/Home"
echo

if [ -f $JAVA_HOME/bin/java ]; then
    echo "Starting examples app with JDK from environment variable JAVA_HOME ($JAVA_HOME)..."
    $JAVA_HOME/bin/java ${jvmOptions} -cp "${mainClasspath}" ${mainClass} "$*"
else
    echo "Starting examples app with java from environment variable PATH..."
    java ${jvmOptions} -cp "${mainClasspath}" ${mainClass} "$*"
fi

if [ $? != 0 ]; then
    echo
    echo "ERROR Failed running the java command."
    echo "Maybe install the latest OpenJDK from (https://adoptium.net/) and check the environment variable JAVA_HOME ($JAVA_HOME)."
    # Prevent the terminal window to disappear before the user has seen the error message
    read -p "Press [Enter] key to close this window." dummyVar
fi
