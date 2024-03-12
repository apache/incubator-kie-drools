/*
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
package org.kie.kogito.quarkus;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartData;
import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;
import io.quarkus.maven.dependency.ArtifactKey;

import static io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog.Language.JAVA;

public class ServerlessWorkflowCodestartTest {

    @RegisterExtension
    public static QuarkusCodestartTest codestartTest = QuarkusCodestartTest.builder()
            .setupStandaloneExtensionTest("org.apache.kie.sonataflow:sonataflow-quarkus")
            .extension(ArtifactKey.fromString("io.quarkus:quarkus-config-yaml"))
            .putData(QuarkusCodestartData.QuarkusDataKey.APP_CONFIG, Map.of("quarkus.devservices.enabled", "false"))
            .languages(JAVA)
            .build();

    @Test
    void testContent() throws Throwable {
        codestartTest.checkGeneratedTestSource("org.acme.GreetTest");
        codestartTest.assertThatGeneratedFileMatchSnapshot(JAVA, "src/main/resources/greet.sw.json");
        codestartTest.assertThatGeneratedFileMatchSnapshot(JAVA, "src/test/resources/application.yml");
    }

    @Test
    void buildAllProjectsForLocalUse() throws Throwable {
        codestartTest.buildAllProjects();
    }

}
