/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.events;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.codegen.process.ProcessGenerationUtils.execModelFromProcessFile;
import static org.kie.kogito.codegen.process.events.CloudEventsResourceGenerator.EMITTER_PREFIX;

class CloudEventsResourceGeneratorTest {

    private final DependencyInjectionAnnotator annotator = new CDIDependencyInjectionAnnotator();

    @Test
    void verifyBasicGenerationCase() {
        final String sourceCode = new CloudEventsResourceGenerator(Collections.emptyList(), annotator).generate();
        assertNotNull(sourceCode);
        final CompilationUnit clazz = StaticJavaParser.parse(sourceCode);
        assertNotNull(clazz);
        assertThat(clazz.getChildNodes()).isNotEmpty();
        assertThat(clazz.getImports()).contains(
                new ImportDeclaration("org.kie.kogito.events.knative.ce.Printer", false, false));
    }

    @Test
    void generatedFilePath() throws URISyntaxException {
        final String filePath = new CloudEventsResourceGenerator(Collections.emptyList(), annotator).generatedFilePath();
        assertThat(new URI(filePath).toString()).endsWith(".java");
    }

    @Test
    void verifyProcessWithIntermediateEvent() {
        final CloudEventsResourceGenerator generator = new CloudEventsResourceGenerator(
                execModelFromProcessFile("/messageevent/IntermediateCatchEventMessage.bpmn2"), annotator);
        final String source = generator.generate();
        assertThat(source).isNotNull();
        assertThat(generator.getTriggers()).hasSize(1);

        final ClassOrInterfaceDeclaration clazz = StaticJavaParser
                .parse(source)
                .getClassByName("CloudEventListenerResource")
                .orElseThrow(() -> new IllegalArgumentException("Class does not exists"));

        assertThat(clazz.getFields().stream()
                           .filter(f -> f.getAnnotationByName("Channel").isPresent())
                           .count()).isEqualTo(1L);
        assertThat(clazz.getFields().stream()
                           .filter(f -> f.getAnnotationByName("Inject").isPresent())
                           .count()).isEqualTo(2L);
    }

    @Test
    void verifyEmitterVariableNameGen() {
        final CloudEventsResourceGenerator generator = new CloudEventsResourceGenerator(Collections.emptyList(), annotator);
        final Map<String, String> tableTest = new HashMap<>();
        tableTest.put("http://github.com/me/myrepo", EMITTER_PREFIX + "httpgithubcommemyrepo");
        tableTest.put("$%@1234whatever123", EMITTER_PREFIX + "1234whatever123");
        tableTest.put("123.12.34.56", EMITTER_PREFIX + "123123456");
        tableTest.put("this_is_a_test", EMITTER_PREFIX + "thisisatest");
        tableTest.forEach((key, value) -> assertThat(generator.sanitizeEmitterName(key)).isEqualTo(value));
    }
}