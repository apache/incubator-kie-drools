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
package org.kie.kogito.codegen.process.events;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.process.ProcessCodegen;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import static org.assertj.core.api.Assertions.assertThat;

public class CodegenUserTaskTest {

    private static final Path BASE_PATH = Paths.get("src/test/resources/").toAbsolutePath();
    private static final String MESSAGE_USERTASK_SOURCE = "usertask/UserTasksProcess.bpmn2";
    private static final Path MESSAGE_USERTASK_SOURCE_FULL_SOURCE = BASE_PATH.resolve(MESSAGE_USERTASK_SOURCE);

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    public void testRESTApiForMessageStartEvent(KogitoBuildContext.Builder contextBuilder) {

        KogitoBuildContext context = contextBuilder.build();
        ProcessCodegen codeGenerator = ProcessCodegen.ofCollectedResources(
                context,
                CollectedResourceProducer.fromFiles(BASE_PATH, MESSAGE_USERTASK_SOURCE_FULL_SOURCE.toFile()));

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).isNotEmpty();

        List<GeneratedFile> resources = generatedFiles.stream()
                .filter(generatedFile -> generatedFile.relativePath().endsWith("org/kie/kogito/test/UserTasksProcessResource.java"))
                .collect(Collectors.toList());

        if (context.hasRESTForGenerator(codeGenerator)) {
            assertThat(resources).hasSize(1);

            CompilationUnit parsedResource = StaticJavaParser.parse(new String(resources.get(0).contents()));

            List<MethodDeclaration> completeTaskMethods = parsedResource
                    .findAll(MethodDeclaration.class, md -> md.getNameAsString().startsWith("completeTask"));

            assertThat(completeTaskMethods).hasSize(2);

            completeTaskMethods.forEach(completeTaskMethod -> assertThat(context.getRestAnnotator().getEndpointValue(completeTaskMethod)
                    .map(url -> url.contains("/{id}/FirstTask/{taskId}") ||
                            url.contains("/{id}/SecondTask/{taskId}"))
                    .orElse(true)).isTrue());
        } else {
            assertThat(resources).isEmpty();
        }

    }
}
