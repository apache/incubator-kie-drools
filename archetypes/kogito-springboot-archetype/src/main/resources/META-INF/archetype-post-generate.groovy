/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Performs the post processing tasks in the generated project

import groovy.xml.XmlParser
import groovy.xml.XmlUtil
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

log = LoggerFactory.getLogger("org.apache.maven")

/**
 * Verify if the starters in the parameters are valid and convert them into actual artifact ids
 */
def startersToArtifactIds(String starters) {
    if (starters == "" || starters == null) {
        return []
    }
    def validStarters = [
            [id: "processes", starter: "kogito-processes-spring-boot-starter"],
            [id: "rules", starter: "kogito-rules-spring-boot-starter"],
            [id: "decisions", starter: "kogito-decisions-spring-boot-starter"],
            [id: "serverless-workflows", starter: "kogito-serverless-workflows-spring-boot-starter"],
            [id: "predictions", starter: "kogito-predictions-spring-boot-starter"]
    ]
    def startersList = starters.split(",")
    return startersList.collect { starterId ->
        {
            def found = validStarters.find { it -> it.id == starterId }
            if (found == null) {
                log.warn("Can't find supported Kogito Spring Boot Starter with id '{}'. Make sure that the starter id is correct. Skipping.", starterId)
                return null
            }
            return found.starter
        }
    }.findAll { it -> it != null }
}

/**
 * Convert the addons list to actual Kogito Addons artifacts Ids
 */
def addonsToArtifactsIds(String addons) {
    if (addons == "" || addons == null) {
        return []
    }
    // this list should be maintained manually for now for each generic/spring boot add-on we create
    // see: https://issues.redhat.com/browse/KOGITO-5619
    def validAddons = [
            [id: "persistence-filesystem", addon: "kogito-addons-persistence-filesystem"],
            [id: "persistence-infinispan", addon: "kogito-addons-persistence-infinispan"],
            [id: "persistence-jdbc", addon: "kogito-addons-persistence-jdbc"],
            [id: "persistence-mongodb", addon: "kogito-addons-persistence-mongodb"],
            [id: "persistence-postgresql", addon: "kogito-addons-persistence-postgresql"],
            [id: "human-task-prediction-api", addon: "kogito-addons-human-task-prediction-api"],
            [id: "cloudevents", addon: "kogito-addons-springboot-cloudevents"],
            [id: "events-decisions", addon: "kogito-addons-springboot-events-decisions"],
            [id: "events-kafka", addon: "kogito-addons-springboot-events-kafka"],
            [id: "explainability", addon: "kogito-addons-springboot-explainability"],
            [id: "jobs-management", addon: "kogito-addons-springboot-jobs-management"],
            [id: "mail", addon: "kogito-addons-springboot-mail"],
            [id: "monitoring-elastic", addon: "kogito-addons-springboot-monitoring-elastic"],
            [id: "monitoring-prometheus", addon: "kogito-addons-springboot-monitoring-prometheus"],
            [id: "process-management", addon: "kogito-addons-springboot-process-management"],
            [id: "process-svg", addon: "kogito-addons-springboot-process-svg"],
            [id: "task-management", addon: "kogito-addons-springboot-task-management"],
            [id: "task-notification", addon: "kogito-addons-springboot-task-notification"],
            [id: "tracing-decision", addon: "kogito-addons-springboot-tracing-decision"]
    ]
    def addonsList = addons.split(",")
    return addonsList.collect { addonId ->
        {
            def found = validAddons.find { it -> it.id == addonId }
            if (found == null) {
                log.warn("Can't find supported Kogito Add-On with id '{}'. Make sure that the add-on id is correct. Skipping.", addonId)
                return null
            }
            return found.addon
        }
    }.findAll { it -> it != null }
}

/**
 * Add the given comma separated list of starters to the generated POM
 */
def addDependenciesToPOM(String starters, String addons) {
    def artifacts = startersToArtifactIds(starters)
    if (artifacts.isEmpty()) {
        artifacts << "kogito-spring-boot-starter"
    }
    artifacts = artifacts.plus(addonsToArtifactsIds(addons))

    def pomPath = Paths.get(request.getOutputDirectory(), request.getArtifactId(), "pom.xml")
    def pomXml = new XmlParser().parse(pomPath.toFile())
    artifacts.each { artifact ->
        def depNode = new Node(null, "dependency")
        depNode.appendNode("groupId", null, "org.kie.kogito")
        depNode.appendNode("artifactId", null, artifact)
        depNode.appendNode("version", null, '${kogito.version}')
        pomXml.dependencies[0].children().add(0, depNode)
    }
    def writer = new FileWriter(pomPath.toString())
    // removing unnecessary white spaces
    XmlUtil.serialize(XmlUtil.serialize(pomXml).trim().replace("\n", "").replaceAll("( *)<", "<"), writer)
}

/**
 * Remove the resources that requires a specific starter
 */
def removeUnneededResources(String starters, String appPackage) {
    if (starters == "" || starters == null) {
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
    if (!starters.contains("decisions") || !starters.contains("predictions") || !starters.contains("processes")) {
        // no need to keep DMN files
        Files.deleteIfExists(projectPath.resolve("src/main/resources/Traffic Violation.dmn"))
        Files.deleteIfExists(projectPath.resolve("src/test/java/" + packagePath + "/TrafficViolationTest.java"))
    }
}

Properties properties = request.getProperties()
String startersProps = properties.get("starters")
String addonsProps = properties.get("addons")
String appPackage = properties.get("package")

addDependenciesToPOM(startersProps, addonsProps)
removeUnneededResources(startersProps, appPackage)
