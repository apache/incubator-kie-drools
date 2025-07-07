/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.springboot.archetypes

import spock.lang.Specification
import groovy.xml.XmlParser

import java.nio.charset.StandardCharsets

// we can't use XML with the current Archetype plugin see: https://github.com/apache/maven-archetype/pull/58

class AddDependencyToPomWithTextReplaceTest extends Specification {
    def pomFile = "/archetype-resources/pom.xml";

    def "Add a list of new dependencies to original pom.xml"() {
        given:
        String[] artifacts = "messaging,persistence,monitoring".split(",")
        def dependencies = new StringBuilder()
        artifacts.each {
            dependencies <<
                    '    <dependency>\n' +
                    '       <groupId>org.kie.kogito</groupId>\n' +
                    '       <artifactId>' + it + '</artifactId>\n' +
                    '       <version>${project.version}</version>\n' +
                    '     </dependency>\n'
        }

        when:
        String pomFile = new String(this.getClass().getResourceAsStream(pomFile).readAllBytes(), StandardCharsets.UTF_8)
                .replace("    <!-- kogito dependencies -->", dependencies)

        then:
        Node pomXml = new XmlParser().parseText(pomFile)
        pomXml.depthFirst().dependencies.dependency.size() == 9
    }

}
