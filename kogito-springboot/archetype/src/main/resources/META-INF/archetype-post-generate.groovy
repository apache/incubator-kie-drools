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
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

log = LoggerFactory.getLogger("org.apache.maven")

/**
 * Verify if the starters in the parameters are valid and convert them into actual artifact ids
 */
def startersToArtifactIds(String starters) {
    if (starters == "_UNDEFINED_" || starters == "" || starters == null) {
        return []
    }
    def validStarters = [
            [id: "processes", starter: "jbpm-spring-boot-starter"],
            [id: "rules", starter: "drools-rules-spring-boot-starter"],
            [id: "decisions", starter: "drools-decisions-spring-boot-starter"],
            [id: "predictions", starter: "kie-predictions-spring-boot-starter"]
    ]
    def startersList = starters.split(",")
    return startersList.collect { starterId ->
        def found = validStarters.find { it -> it.id == starterId }
        if (found == null) {
            log.warn("Can't find supported Kogito Spring Boot Starter with id '{}'. Make sure that the starter id is correct. Skipping.", starterId)
            return null
        }
        return found.starter
    }.findAll { it -> it != null }
}

def resolveAddonGroupId(String artifactId) {
    switch (artifactId) {
        case { artifactId.startsWith("drools-") }:
            return "org.drools"
        case { artifactId.startsWith("jbpm-") }:
            return "org.jbpm"
        case { artifactId.startsWith("sonataflow-") }:
            return "org.apache.kie.sonataflow"
        default: return "org.kie"
    }
}

/**
 * Convert the addons list to actual Kogito Addons artifacts Ids
 */
def addonsToArtifactsIds(String addons) {
    if (addons == "_UNDEFINED_" || addons == "" || addons == null) {
        return []
    }
    // this list should be maintained manually for now for each generic/spring boot add-on we create
    // see: https://issues.redhat.com/browse/KOGITO-5619
    def validAddons = [
            [id: "persistence-filesystem", addon: "kie-addons-springboot-persistence-filesystem"],
            [id: "persistence-infinispan", addon: "kie-addons-springboot-persistence-infinispan"],
            [id: "persistence-jdbc", addon: "kie-addons-springboot-persistence-jdbc"],
            [id: "persistence-mongodb", addon: "kie-addons-springboot-persistence-mongodb"],
            [id: "persistence-postgresql", addon: "kie-addons-springboot-persistence-postgresql"],
            [id: "human-task-prediction-api", addon: "kogito-addons-human-task-prediction-api"],
            [id: "messaging", addon: "kie-addons-springboot-messaging"],
            [id: "events-decisions", addon: "kie-addons-springboot-events-decisions"],
            [id: "events-process-kafka", addon: "kie-addons-springboot-events-process-kafka"],
            [id: "explainability", addon: "kie-addons-springboot-explainability"],
            [id: "jobs-management", addon: "kogito-addons-springboot-jobs-management"],
            [id: "mail", addon: "jbpm-addons-springboot-mail"],
            [id: "monitoring-elastic", addon: "kie-addons-springboot-monitoring-elastic"],
            [id: "monitoring-prometheus", addon: "kie-addons-springboot-monitoring-prometheus"],
            [id: "process-management", addon: "kie-addons-springboot-process-management"],
            [id: "process-svg", addon: "kie-addons-springboot-process-svg"],
            [id: "task-management", addon: "jbpm-addons-springboot-task-management"],
            [id: "task-notification", addon: "jbpm-addons-springboot-task-notification"],
            [id: "tracing-decision", addon: "kie-addons-springboot-tracing-decision"]
    ]
    def addonsList = addons.split(",")
    return addonsList.collect { addonId ->
        def found = validAddons.find { it -> it.id == addonId }
        if (found == null) {
            log.warn("Can't find supported Kogito Add-On with id '{}'. Make sure that the add-on id is correct. Skipping.", addonId)
            return null
        }
        return found.addon
    }.findAll { it -> it != null }
}

/**
 * Add the given comma separated list of starters to the generated POM
 */
def addDependenciesToPOM(String starters, String addons) {
    def artifacts = startersToArtifactIds(starters)
    if (artifacts.isEmpty()) {
        artifacts << "jbpm-with-drools-spring-boot-starter"
    }
    artifacts = artifacts + addonsToArtifactsIds(addons)
    def dependencies = new StringBuilder()
    artifacts.each { artifact ->
            dependencies <<
                    '    <dependency>\n' +
                    '       <groupId>' + resolveAddonGroupId(artifact) + '</groupId>\n' +
                    '       <artifactId>' + artifact + '</artifactId>\n' +
                    '    </dependency>\n';
    }
    def pomPath = Paths.get(request.getOutputDirectory(), request.getArtifactId(), "pom.xml")
    def pomFile = Files.readString(pomPath).replace("    <!-- kogito dependencies -->", dependencies)
    pomPath.toFile().withWriter("utf-8") { writer -> writer.write(pomFile) }
}

/**
 * Remove the resources that requires a specific starter
 */
def removeUnneededResources(String starters, String appPackage) {
    if (starters == "_UNDEFINED_" || starters == "" || starters == null) {
        // in this case we will have all starters in the project, let's include everything in the final project
        return
    }
    Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
    String packagePath = appPackage.replace(".", File.separator)
    if (!starters.contains("processes")) {
        // no need to keep BPMN files
        Files.deleteIfExists(projectPath.resolve("src/main/resources/test-process.bpmn2"))
        Files.deleteIfExists(projectPath.resolve("src/test/java/" + packagePath + "/GreetingsTest.java"))
    }
    if (!starters.contains("decisions") && !starters.contains("predictions")) {
        // no need to keep DMN files
        Files.deleteIfExists(projectPath.resolve("src/main/resources/TrafficViolation.dmn"))
        Files.deleteIfExists(projectPath.resolve("src/test/java/" + packagePath + "/TrafficViolationTest.java"))
    }
}

Properties properties = request.getProperties()
String startersProps = properties.get("starters")
String addonsProps = properties.get("addons")
String appPackage = properties.get("package")

addDependenciesToPOM(startersProps, addonsProps)
removeUnneededResources(startersProps, appPackage)
