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

/*
Use this file to test the changes in the archetype-post-generate.groovy
 */

class AddDependencyToPomWithXmlTest extends Specification {
    def pomFile = "/archetype-resources/pom.xml";

    def "Original pom.xml has 5 dependencies"() {
        given:
        Node pomXml = new XmlParser().parse(this.getClass().getResourceAsStream(pomFile))

        expect:
        pomXml.depthFirst().dependencies.dependency.size() == 6
    }

    def "Add a new dependency to original pom.xml"() {
        given:
        Node pomXml = new XmlParser().parse(this.getClass().getResourceAsStream(pomFile))

        when:
        Node newDep = new Node(null, "dependency",
                [groupId: "org.kie", artifactId: "kie-addons-springboot-messaging", version: "999-SNAPSHOT"])
        pomXml.dependencies[0].children().add(0, newDep)

        then:
        pomXml.depthFirst().dependencies.dependency.size() == 7
    }

    def "Add a list of new dependencies to original pom.xml"() {
        given:
        String[] artifacts = "messaging,persistence,monitoring".split(",")
        Node pomXml = new XmlParser().parse(this.getClass().getResourceAsStream(pomFile))

        when:
        for (String artifact : artifacts) {
            Node depNode = new Node(null, "dependency")
            depNode.appendNode("version", null, '${kogito.version}')
            depNode.appendNode("groupId", null, "org.kie")
            depNode.appendNode("artifactId", null, "kogito-addons-springboot-" + artifact)
            pomXml.dependencies[0].children().add(0, depNode)
        }

        then:
        pomXml.depthFirst().dependencies.dependency.size() == 9

    }
}