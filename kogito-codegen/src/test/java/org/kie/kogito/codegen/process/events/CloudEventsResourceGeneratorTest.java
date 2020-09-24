package org.kie.kogito.codegen.process.events;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CloudEventsResourceGeneratorTest {

    @Test
    void verifyBasicGenerationCase() {
        final String sourceCode = new CloudEventsResourceGenerator(Collections.emptyList()).generate();
        assertNotNull(sourceCode);
        final CompilationUnit clazz = StaticJavaParser.parse(sourceCode);
        assertNotNull(clazz);
        assertThat(clazz.getChildNodes()).isNotEmpty();
        assertThat(clazz.getImports()).contains(
                new ImportDeclaration("org.kie.kogito.events.knative.ce.Printer", false, false));
    }

    @Test
    void generatedFilePath() throws URISyntaxException {
        final String filePath = new CloudEventsResourceGenerator(Collections.emptyList()).generatedFilePath();
        assertThat(new URI(filePath).toString()).endsWith(".java");
    }

    @Test
    void verifyProcessWithIntermediateEvent() {
        final CloudEventsResourceGenerator generator =
                new CloudEventsResourceGenerator(ProcessGenerationUtils.execModelFromProcessFile("/messageevent/IntermediateCatchEventMessage.bpmn2"));
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
        final CloudEventsResourceGenerator generator = new CloudEventsResourceGenerator(Collections.emptyList());
        final List<String> names = new ArrayList<>();
        // 50 message nodes in one process won't clash their names?
        for (int i = 1; i <= 50; i++) {
            final String name = generator.generateRandomEmitterName();
            assertThat(names.stream().anyMatch(n -> n.equals(name))).isFalse();
            names.add(name);
        }
        assertThat(names).hasSize(50);
    }

}