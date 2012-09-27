#!/bin/sh

# Change directory to the directory of the script
cd `dirname $0`

mainClasspath=
for i in binaries/*.jar; do mainClasspath=${mainClasspath}:$i; done
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
if [ -d $JAVA_HOME/bin ]; then
    $JAVA_HOME/bin/java -Xms256m -Xmx512m -server -cp ${mainClasspath} ${mainClass} $*
else
    java -Xms256m -Xmx512m -cp ${mainClasspath} ${mainClass} $*
fi

if [ $? != 0 ] ; then
    echo
    echo "Error occurred. Check if \$JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    sleep 20
fi
