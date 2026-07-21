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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Check the generated resources - just decision resources should be added in the custom-decision-project
Path projectPath = basedir.toPath().resolve("project/custom-decision-project")
assert !Files.exists(projectPath.resolve("src/main/resources/test-process.bpmn2"))
assert !Files.exists(projectPath.resolve("src/test/java/it/pkg/GreetingsTest.java"))
assert Files.exists(projectPath.resolve("src/main/resources/TrafficViolation.dmn"))
assert Files.exists(projectPath.resolve("src/test/java/it/pkg/TrafficViolationTest.java"))

// Check starters in pom.xml - decisions starter specified, so drools-decisions-spring-boot-starter should be added in the basic-project
String pomContent = Files.readString(projectPath.resolve("pom.xml"))
assert pomContent.contains("drools-decisions-spring-boot-starter")
assert !pomContent.contains("jbpm-with-drools-spring-boot-starter")

// Also no addons were specified
assert !pomContent.contains("addon")