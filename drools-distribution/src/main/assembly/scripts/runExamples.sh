#!/bin/sh

#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

# Change directory to the directory of the script
cd `dirname $0`

mainClass=org.drools.examples.DroolsExamplesApp
mainClasspath="binaries/*:../binaries/*"

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
if [ -f $JAVA_HOME/bin/java ]; then
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
