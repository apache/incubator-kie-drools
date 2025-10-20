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
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.process.ProcessCodegen;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import static org.assertj.core.api.Assertions.assertThat;

public class CodegenMessageStartEventTest {

    private static final Path BASE_PATH = Paths.get("src/test/resources/").toAbsolutePath();
    private static final String MESSAGE_START_EVENT_SOURCE = "messagestartevent/MessageStartEvent.bpmn2";
    private static final Path MESSAGE_START_EVENT_SOURCE_FULL_SOURCE = BASE_PATH.resolve(MESSAGE_START_EVENT_SOURCE);
    private static final String MESSAGE_END_EVENT_SOURCE = "messagestartevent/MessageEndEvent.bpmn2";
    private static final Path MESSAGE_END_EVENT_SOURCE_FULL_SOURCE = BASE_PATH.resolve(MESSAGE_END_EVENT_SOURCE);
    private static final String MESSAGE_START_END_EVENT_SOURCE = "messagestartevent/MessageStartAndEndEvent.bpmn2";
    private static final Path MESSAGE_START_END_EVENT_SOURCE_FULL_SOURCE = BASE_PATH.resolve(MESSAGE_START_END_EVENT_SOURCE);

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    public void testRESTApiForMessageStartEvent(KogitoBuildContext.Builder contextBuilder) {

        KogitoBuildContext context = contextBuilder.build();
        ProcessCodegen codeGenerator = ProcessCodegen.ofCollectedResources(
                context,
                CollectedResourceProducer.fromFiles(BASE_PATH, MESSAGE_START_EVENT_SOURCE_FULL_SOURCE.toFile()));

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).isNotEmpty();

        List<GeneratedFile> resources = generatedFiles.stream()
                .filter(generatedFile -> generatedFile.relativePath().endsWith("org/kie/kogito/test/MessageStartEventResource.java"))
                .collect(Collectors.toList());

        if (context.hasRESTForGenerator(codeGenerator)) {
            assertThat(resources).hasSize(1);

            CompilationUnit parsedResource = StaticJavaParser.parse(new String(resources.get(0).contents()));

            assertThat(parsedResource
                    .findFirst(MethodDeclaration.class, md -> md.getNameAsString().startsWith("createResource")))
                            .withFailMessage("For processes without none start event there should not be create resource method")
                            .isEmpty();
        } else {
            assertThat(resources).isEmpty();
        }

    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    public void testRESTApiForMessageEndEvent(KogitoBuildContext.Builder contextBuilder) {

        KogitoBuildContext context = contextBuilder.build();
        ProcessCodegen codeGenerator = ProcessCodegen.ofCollectedResources(
                context,
                CollectedResourceProducer.fromFiles(BASE_PATH, MESSAGE_END_EVENT_SOURCE_FULL_SOURCE.toFile()));

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).isNotEmpty();

        List<GeneratedFile> resources = generatedFiles.stream()
                .filter(generatedFile -> generatedFile.relativePath().endsWith("org/kie/kogito/test/MessageStartEventResource.java"))
                .collect(Collectors.toList());

        if (context.hasRESTForGenerator(codeGenerator)) {
            assertThat(resources).hasSize(1);

            CompilationUnit parsedResource = StaticJavaParser.parse(new String(resources.get(0).contents()));

            assertThat(parsedResource
                    .findAll(MethodDeclaration.class, md -> md.getNameAsString().startsWith("createResource")))
                            .withFailMessage("Must have method with name 'createResource'")
                            .hasSize(1);
        } else {
            assertThat(resources).isEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void testMessageProducerForMessageEndEvent(KogitoBuildContext.Builder contextBuilder) {
        Properties properties = new Properties();
        properties.put("kogito.addon.cloudevents.kafka.kogito_outgoing_stream", "test-out");
        properties.put("kogito.addon.cloudevents.kafka.kogito_incoming_stream", "test-in");
        contextBuilder.withApplicationProperties(properties);

        KogitoBuildContext context = contextBuilder.build();
        ProcessCodegen codeGenerator = ProcessCodegen.ofCollectedResources(
                context,
                CollectedResourceProducer.fromFiles(BASE_PATH, MESSAGE_START_END_EVENT_SOURCE_FULL_SOURCE.toFile()));

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).isNotEmpty();

        // class name is with suffix that represents node id as there might be multiple end message events
        List<GeneratedFile> resources = generatedFiles.stream()
                .filter(generatedFile -> generatedFile.relativePath().endsWith("org/kie/kogito/test/MessageStartEventMessageProducer_EndEvent_1.java"))
                .collect(Collectors.toList());
        assertThat(resources).hasSize(1);

        CompilationUnit parsedResource = StaticJavaParser.parse(new String(resources.get(0).contents()));

        if (context.hasDI()) {
            assertThat(parsedResource
                    .findAll(ClassOrInterfaceDeclaration.class, cd -> cd.getExtendedTypes().stream().anyMatch(et -> et.getNameAsString().endsWith("AbstractMessageProducer"))))
                            .withFailMessage("Must extends class 'AbstractMessageProducer'")
                            .hasSize(1);
        } else {
            assertThat(parsedResource
                    .findAll(MethodDeclaration.class, md -> md.getNameAsString().equals("produce")))
                            .withFailMessage("Must have method with name 'produce'")
                            .hasSize(1);
        }
    }
}
