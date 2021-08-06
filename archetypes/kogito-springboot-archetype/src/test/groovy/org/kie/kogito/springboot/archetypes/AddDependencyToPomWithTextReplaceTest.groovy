package org.kie.kogito.springboot.archetypes

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

// we can't use XML with the current Archetype plugin see: https://github.com/apache/maven-archetype/pull/58

class AddDependencyToPomWithTextReplaceTest extends Specification {
    def pomFile = "/archetype-resources/pom.xml";

    def "Add a list of new dependencies to original pom.xml"() {
        given:
        String[] artifacts = "cloudevents,persistence,monitoring".split(",")
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
        String pomFile = Files.readString(Path.of(this.getClass().getResource(pomFile).toURI()))
                .replace("    <!-- kogito dependencies -->", dependencies)

        then:
        Node pomXml = new XmlParser().parseText(pomFile)
        pomXml.depthFirst().dependencies.dependency.size() == 7
    }

}
