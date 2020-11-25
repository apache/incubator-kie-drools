/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.metadata.MetaDataWriter;
import org.kie.kogito.codegen.metadata.PrometheusLabeler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApplicationGeneratorTest {

    private static final String PACKAGE_NAME = "org.drools.test";
    private static final String EXPECTED_APPLICATION_NAME = PACKAGE_NAME + ".Application";

    @Test
    public void targetCanonicalName() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File(""));
        assertThat(appGenerator.targetCanonicalName()).isNotNull();
        assertThat(appGenerator.targetCanonicalName()).isEqualTo(EXPECTED_APPLICATION_NAME);
    }

    @Test
    public void packageNameNull() {
        assertThatThrownBy(() -> new ApplicationGenerator(null, new File("")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void packageNameInvalid() {
        assertThatThrownBy(() -> new ApplicationGenerator("i.am.an-invalid.package-name.sorry", new File("")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void generatedFilePath() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File(""));
        String path = appGenerator.generateApplicationDescriptor().relativePath();
        assertThat(path).isNotNull();
        assertThat(path).isEqualTo(EXPECTED_APPLICATION_NAME.replace(".", "/") + ".java");
    }

    @Test
    public void compilationUnit() {
        final ApplicationContainerGenerator appGenerator = new ApplicationContainerGenerator(PACKAGE_NAME);
        assertCompilationUnit(appGenerator.getCompilationUnitOrThrow(), false);
    }

    @Test
    public void compilationUnitWithCDI() {
        final ApplicationContainerGenerator appGenerator = new ApplicationContainerGenerator(PACKAGE_NAME);
        appGenerator.withDependencyInjection(new CDIDependencyInjectionAnnotator());
        assertCompilationUnit(appGenerator.getCompilationUnitOrThrow(), true);
    }

    @Test
    public void generateWithMonitoring() throws IOException {
        final Path targetDirectory = Paths.get("target");
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, targetDirectory.toFile()).withAddons(new AddonsConfig().withPrometheusMonitoring(true));
        appGenerator.generate();
        assertImageMetadata(targetDirectory, new PrometheusLabeler().generateLabels());
    }

    @Test
    public void writeLabelsImageMetadata() throws IOException {
        final Path targetDirectory = Paths.get("target");
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, targetDirectory.toFile());

        final Map<String, String> labels = new HashMap<>();
        labels.put("testKey1", "testValue1");
        labels.put("testKey2", "testValue2");
        labels.put("testKey3", "testValue3");

        MetaDataWriter.writeLabelsImageMetadata(targetDirectory.toFile(), labels);
        assertImageMetadata(targetDirectory, labels);
    }

    private void assertImageMetadata(final Path directory, final Map<String, String> expectedLabels) throws IOException {
        try (Stream<Path> stream = Files.walk(directory, 1)) {
            final Optional<Path> generatedFile = stream
                    .filter(file -> file.getFileName().toString().equals("image_metadata.json"))
                    .findFirst();
            assertThat(generatedFile).isPresent();

            ObjectMapper mapper = new ObjectMapper();
            final Map<String, List> elementsFromFile = mapper.readValue(generatedFile.get().toFile(),
                                                                        new TypeReference<Map<String, List>>() {
                                                                        });
            assertThat(elementsFromFile).hasSize(1);
            final List<Map<String, String>> listWithLabelsMap = elementsFromFile.entrySet().iterator().next().getValue();
            assertThat(listWithLabelsMap).isNotNull();
            assertThat(listWithLabelsMap).hasSize(1);
            assertThat(listWithLabelsMap.get(0)).containsAllEntriesOf(expectedLabels);
        }
    }

    private void assertCompilationUnit(final CompilationUnit compilationUnit, final boolean checkCDI) {
        assertThat(compilationUnit).isNotNull();

        assertThat(compilationUnit.getPackageDeclaration()).isPresent();
        assertThat(compilationUnit.getPackageDeclaration().get().getName().toString()).isEqualTo(PACKAGE_NAME);

        assertThat(compilationUnit.getTypes()).isNotNull();
        assertThat(compilationUnit.getTypes()).hasSize(1);

        final TypeDeclaration mainAppClass = compilationUnit.getTypes().get(0);
        assertThat(mainAppClass).isNotNull();
        assertThat(mainAppClass.getName().toString()).isEqualTo("Application");

        if (checkCDI) {
            assertThat(mainAppClass.getAnnotations()).isNotEmpty();
            assertThat(mainAppClass.getAnnotationByName("Singleton")).isPresent();
        } else {
            assertThat(mainAppClass.getAnnotationByName("Singleton")).isNotPresent();
        }

        assertThat(mainAppClass.getMembers()).isNotNull();
    }

    private void assertGeneratedFiles(final Collection<GeneratedFile> generatedFiles,
                                      final byte[] expectedApplicationContent,
                                      final int expectedFilesCount) {
        assertThat(generatedFiles).isNotNull();
        assertThat(generatedFiles).hasSize(expectedFilesCount);

        for (GeneratedFile generatedFile : generatedFiles) {
            assertThat(generatedFile).isNotNull();
            assertThat(generatedFile.getType()).isIn(GeneratedFile.Type.APPLICATION, GeneratedFile.Type.APPLICATION_CONFIG, GeneratedFile.Type.APPLICATION_SECTION, GeneratedFile.Type.RULE, GeneratedFile.Type.CLASS);
            if (generatedFile.getType() == GeneratedFile.Type.APPLICATION) {
                if (generatedFile.relativePath() == EXPECTED_APPLICATION_NAME.replace(".", "/") + ".java") {
                    assertThat(generatedFile.contents()).isEqualTo(expectedApplicationContent);
                }
            }
        }
    }
}
