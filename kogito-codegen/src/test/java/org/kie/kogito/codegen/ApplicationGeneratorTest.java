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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Config;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void generatedFilePath() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File(""));
        assertThat(appGenerator.generatedFilePath()).isNotNull();
        assertThat(appGenerator.generatedFilePath()).isEqualTo(EXPECTED_APPLICATION_NAME.replace(".", "/") + ".java");
    }

    @Test
    public void compilationUnit() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File("target"));
        assertCompilationUnit(appGenerator.compilationUnit(), false, 0);
    }

    @Test
    public void compilationUnitWithCDI() {
        final ApplicationGenerator initialAppGenerator = new ApplicationGenerator(PACKAGE_NAME, new File("target"));
        final ApplicationGenerator appGenerator = initialAppGenerator.withDependencyInjection(new CDIDependencyInjectionAnnotator());
        assertThat(appGenerator).isSameAs(initialAppGenerator);
        assertCompilationUnit(appGenerator.compilationUnit(), true, 0);
    }

    @Test
    public void compilationUnitWithFactoryMethods() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File("target"));
        final String testMethodName = "testMethod";
        final MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setName(testMethodName);

        appGenerator.addFactoryMethods(Collections.singleton(methodDeclaration));

        final CompilationUnit compilationUnit = appGenerator.compilationUnit();
        assertCompilationUnit(compilationUnit, false, 1);

        final TypeDeclaration mainAppClass = compilationUnit.getTypes().get(0);
        assertThat(mainAppClass.getMembers())
                .filteredOn(member -> member instanceof MethodDeclaration
                        && ((MethodDeclaration) member).getName().toString().equals(testMethodName))
                .hasSize(1);
    }

    @Test
    public void generate() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File("target"));
        final Collection<GeneratedFile> generatedFiles = appGenerator.generate();
        assertGeneratedFiles(generatedFiles, appGenerator.compilationUnit().toString().getBytes(StandardCharsets.UTF_8), 1);
    }

    @Test
    public void generateWithOtherGenerator() throws IOException {
        final Generator mockGenerator = Mockito.mock(Generator.class);

        ApplicationSection appSection = new ApplicationSection() {

            private ClassOrInterfaceDeclaration classOrInterfaceDeclaration =
                    new ClassOrInterfaceDeclaration().setName("Foo");
            private MethodDeclaration methodDeclaration =
                    new MethodDeclaration().setType("void");
            private FieldDeclaration fieldDeclaration =
                    new FieldDeclaration().addVariable(new VariableDeclarator().setType(int.class).setName("i"));

            @Override
            public FieldDeclaration fieldDeclaration() {
                return fieldDeclaration;
            }

            @Override
            public MethodDeclaration factoryMethod() {
                return methodDeclaration;
            }

            @Override
            public ClassOrInterfaceDeclaration classDeclaration() {
                return classOrInterfaceDeclaration;
            }
        };

        when(mockGenerator.section()).thenReturn(appSection);

        final GeneratedFile generatedFile = mock(GeneratedFile.class);
        when(generatedFile.getType()).thenReturn(GeneratedFile.Type.RULE);
        final Collection<GeneratedFile> mockFiles = Collections.singleton(generatedFile);
        when(mockGenerator.generate()).thenReturn(mockFiles);

        final Map<String, String> mockLabels = new HashMap<>();
        mockLabels.put("testKey", "testValue");
        when(mockGenerator.getLabels()).thenReturn(mockLabels);

        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, new File("target/classes"));
        appGenerator.withGenerator(mockGenerator);

        final Collection<GeneratedFile> generatedFiles = appGenerator.generate();
        final CompilationUnit compilationUnit = appGenerator.compilationUnit();
        assertGeneratedFiles(generatedFiles, compilationUnit.toString().getBytes(StandardCharsets.UTF_8), 2);

        assertCompilationUnit(compilationUnit, false, 3);
        final TypeDeclaration mainAppClass = compilationUnit.getTypes().get(0);
        assertThat(mainAppClass.getMembers()).filteredOn(member -> member == appSection.factoryMethod()).hasSize(1);
        assertThat(mainAppClass.getMembers()).filteredOn(member -> member == appSection.classDeclaration()).hasSize(1);

        assertImageMetadata(Paths.get("target/classes"), mockLabels);
    }

    @Test
    public void writeLabelsImageMetadata() throws IOException {
        final Path targetDirectory = Paths.get("target");
        final ApplicationGenerator appGenerator = new ApplicationGenerator(PACKAGE_NAME, targetDirectory.toFile());

        final Map<String, String> labels = new HashMap<>();
        labels.put("testKey1", "testValue1");
        labels.put("testKey2", "testValue2");
        labels.put("testKey3", "testValue3");

        appGenerator.writeLabelsImageMetadata(labels);
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
                                                                        new TypeReference<Map<String, List>>(){});
            assertThat(elementsFromFile).hasSize(1);
            final List<Map<String, String>> listWithLabelsMap = elementsFromFile.entrySet().iterator().next().getValue();
            assertThat(listWithLabelsMap).isNotNull();
            assertThat(listWithLabelsMap).hasSize(1);
            assertThat(listWithLabelsMap.get(0)).containsAllEntriesOf(expectedLabels);
        }
    }

    private void assertCompilationUnit(final CompilationUnit compilationUnit, final boolean checkCDI) {
        assertCompilationUnit(compilationUnit, checkCDI, 0);
    }

    private void assertCompilationUnit(final CompilationUnit compilationUnit, final boolean checkCDI,
                                       final int expectedNumberOfCustomFactoryMethods) {
        assertThat(compilationUnit).isNotNull();

        assertThat(compilationUnit.getPackageDeclaration()).isPresent();
        assertThat(compilationUnit.getPackageDeclaration().get().getName().toString()).isEqualTo(PACKAGE_NAME);

        assertThat(compilationUnit.getImports()).isNotNull();
        assertThat(compilationUnit.getImports()).hasSize(3);
        assertThat(compilationUnit.getImports().get(0).getName().toString()).isEqualTo(Config.class.getCanonicalName());

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
        assertThat(mainAppClass.getMembers()).hasSize(4 + expectedNumberOfCustomFactoryMethods);

        assertThat(mainAppClass.getMembers())
                .filteredOn(member -> member instanceof MethodDeclaration
                        && ((MethodDeclaration) member).getName().toString().equals("config")
                        && !((MethodDeclaration) member).isStatic())
                .hasSize(1);

        assertThat(mainAppClass.getMembers())
                .filteredOn(member -> member instanceof FieldDeclaration
                        && ((FieldDeclaration) member).getVariable(0).getName().toString().equals("config")
                        && ((FieldDeclaration) member).isStatic())
                .hasSize(1);

        assertThat(mainAppClass.getMember(0)).isInstanceOfAny(MethodDeclaration.class, FieldDeclaration.class);
        assertThat(mainAppClass.getMember(1)).isInstanceOfAny(MethodDeclaration.class, FieldDeclaration.class);
    }

    private void assertGeneratedFiles(final Collection<GeneratedFile> generatedFiles,
                                      final byte[] expectedApplicationContent,
                                      final int expectedFilesCount) {
        assertThat(generatedFiles).isNotNull();
        assertThat(generatedFiles).hasSize(expectedFilesCount);

        for (GeneratedFile generatedFile : generatedFiles) {
            assertThat(generatedFile).isNotNull();
            assertThat(generatedFile.getType()).isIn(GeneratedFile.Type.APPLICATION, GeneratedFile.Type.RULE);
            if (generatedFile.getType() == GeneratedFile.Type.APPLICATION) {
                assertThat(generatedFile.relativePath()).isEqualTo(EXPECTED_APPLICATION_NAME.replace(".", "/") + ".java");
                assertThat(generatedFile.contents()).isEqualTo(expectedApplicationContent);
            }
        }
    }
}
