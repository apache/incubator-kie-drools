package org.kie.kogito.codegen.process.events;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CloudEventsResourceGeneratorTest {

    @Test
    void verifyBasicGenerationCase() {
        final String sourceCode = new CloudEventsResourceGenerator(Collections.emptyList()).generate();
        assertNotNull(sourceCode);
        final CompilationUnit clazz = StaticJavaParser.parse(sourceCode);
        assertNotNull(clazz);
        Assertions.assertThat(clazz.getChildNodes()).isNotEmpty();
        Assertions.assertThat(clazz.getImports()).contains(
                new ImportDeclaration("org.kie.kogito.events.knative.ce.http.RestEasyHttpRequestConverter", false, false));
    }

    @Test
    void generatedFilePath() throws URISyntaxException {
        final String filePath = new CloudEventsResourceGenerator(Collections.emptyList()).generatedFilePath();
        Assertions.assertThat(new URI(filePath).toString()).endsWith(".java");
    }

    @Test
    void verifyProcessWithIntermediateEvent() {
        final CloudEventsResourceGenerator generator =
                new CloudEventsResourceGenerator(ProcessGenerationUtils.execModelFromProcessFile("/messageevent/IntermediateCatchEventMessage.bpmn2"));
        final String source = generator.generate();
        Assertions.assertThat(source).isNotNull();
        Assertions.assertThat(generator.getTriggers()).hasSize(1);

        final ClassOrInterfaceDeclaration clazz = StaticJavaParser
                .parse(source)
                .getClassByName("CloudEventListenerResource")
                .orElseThrow(() -> new IllegalArgumentException("Class does not exists"));

        Assertions.assertThat(clazz.getFields().stream()
                                      .filter(f -> f.getAnnotationByName("Channel").isPresent())
                                      .count()).isEqualTo(1L);
        Assertions.assertThat(clazz.getFields().stream()
                                      .filter(f -> f.getAnnotationByName("Inject").isPresent())
                                      .count()).isEqualTo(1L);
    }
}