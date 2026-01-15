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
package org.kie.kogito.codegen.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.codegen.common.GeneratedFileWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class GeneratedFileManagerTest {

    @TempDir
    Path tempDir;

    private Path testDirectory;

    @BeforeEach
    void setUp() throws IOException {
        testDirectory = tempDir.resolve("test-dir");
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(testDirectory)) {
            Files.walk(testDirectory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignore cleanup errors
                        }
                    });
        }
    }

    @ParameterizedTest
    @MethodSource("provideFileTypeTestCases")
    void dumpGeneratedFiles_withDifferentFileTypes_shouldWriteAllTypes(
            List<String> fileNames, List<String> expectedFiles) throws IOException {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        for (String fileName : fileNames) {
            generatedFiles.add(createMockGeneratedFile(fileName));
        }

        GeneratedFileManager.dumpGeneratedFiles(generatedFiles, testDirectory);

        assertThat(testDirectory).exists();

        // Verify the correct number of files were created
        try (Stream<Path> files = Files.walk(testDirectory)) {
            long fileCount = files.filter(Files::isRegularFile).count();
            assertThat(fileCount).isEqualTo(expectedFiles.size());
        }

        // Verify each expected file exists somewhere in the directory tree
        try (Stream<Path> files = Files.walk(testDirectory)) {
            List<String> actualFileNames = files
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .toList();

            List<String> sortedExpectedFiles = new ArrayList<>(expectedFiles);
            sortedExpectedFiles.sort(String::compareTo);

            assertThat(actualFileNames)
                    .as("All expected files should be written")
                    .containsExactlyElementsOf(sortedExpectedFiles);
        }

        // Verify file content for the first file
        if (!expectedFiles.isEmpty()) {
            try (Stream<Path> files = Files.walk(testDirectory)) {
                Path firstFile = files
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().equals(expectedFiles.get(0)))
                        .findFirst()
                        .orElseThrow();

                String content = Files.readString(firstFile);
                assertThat(content).isEqualTo("// Generated content");
            }
        }
    }

    @Test
    void dumpGeneratedFiles_withNullBasePath_shouldThrowNullPointerException() {
        Collection<GeneratedFile> generatedFiles = createMockGeneratedFiles(1);

        assertThatThrownBy(() -> GeneratedFileManager.dumpGeneratedFiles(generatedFiles, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("basePath must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void dumpGeneratedFiles_withNullOrEmptyCollection_shouldNotWriteFiles(Collection<GeneratedFile> generatedFiles) {
        GeneratedFileManager.dumpGeneratedFiles(generatedFiles, testDirectory);

        // no exception should be thrown and directory should still exist
        assertThat(testDirectory).exists();
    }

    @Test
    void writeGeneratedFile_withValidFile_shouldCallWriter() {
        GeneratedFile generatedFile = createMockGeneratedFile("TestFile.java");
        GeneratedFileWriter writer = mock(GeneratedFileWriter.class);

        GeneratedFileManager.writeGeneratedFile(generatedFile, writer);

        ArgumentCaptor<GeneratedFile> captor = ArgumentCaptor.forClass(GeneratedFile.class);
        verify(writer, times(1)).write(captor.capture());
        assertThat(captor.getValue()).isEqualTo(generatedFile);
    }

    @ParameterizedTest
    @MethodSource("provideExtensionTestCases")
    void deleteFilesByExtension_withVariousExtensions_shouldDeleteMatchingFiles(
            String extension, List<String> filesToCreate, List<String> expectedDeleted) throws IOException {

        for (String fileName : filesToCreate) {
            Path file = testDirectory.resolve(fileName);
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }

        GeneratedFileManager.deleteFilesByExtension(testDirectory, extension);

        for (String fileName : filesToCreate) {
            Path file = testDirectory.resolve(fileName);
            if (expectedDeleted.contains(fileName)) {
                assertThat(file).doesNotExist();
            } else {
                assertThat(file).exists();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { ".java", "java", ".JAVA", "JAVA" })
    void deleteFilesByExtension_withDifferentExtensionFormats_shouldNormalizeAndDelete(String extension) throws IOException {
        Path javaFile = testDirectory.resolve("Test.java");
        Path txtFile = testDirectory.resolve("Test.txt");
        Files.createFile(javaFile);
        Files.createFile(txtFile);

        GeneratedFileManager.deleteFilesByExtension(testDirectory, extension);

        assertThat(javaFile).doesNotExist();
        assertThat(txtFile).exists();
    }

    @Test
    void deleteFilesByExtension_withNestedDirectories_shouldDeleteRecursively() throws IOException {
        Path subDir = testDirectory.resolve("subdir");
        Files.createDirectories(subDir);
        Path file1 = testDirectory.resolve("file1.drl");
        Path file2 = subDir.resolve("file2.drl");
        Path file3 = subDir.resolve("file3.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        Files.createFile(file3);

        GeneratedFileManager.deleteFilesByExtension(testDirectory, ".drl");

        assertThat(file1).doesNotExist();
        assertThat(file2).doesNotExist();
        assertThat(file3).exists();
    }

    @Test
    void deleteFilesByExtension_withNonExistentDirectory_shouldThrowIllegalArgumentException() {
        Path nonExistentDir = tempDir.resolve("non-existent");

        assertThatThrownBy(() -> GeneratedFileManager.deleteFilesByExtension(nonExistentDir, ".java"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("directory does not exist");
    }

    @Test
    void deleteFilesByExtension_withEmptyDirectory_shouldNotThrowException() throws IOException {
        Path emptyDir = testDirectory.resolve("empty");
        Files.createDirectories(emptyDir);

        GeneratedFileManager.deleteFilesByExtension(emptyDir, ".java");
        assertThat(emptyDir).exists();
    }

    @Test
    void deleteFilesByExtension_withNoMatchingFiles_shouldNotDeleteAnything() throws IOException {
        Path file1 = testDirectory.resolve("file1.txt");
        Path file2 = testDirectory.resolve("file2.xml");
        Files.createFile(file1);
        Files.createFile(file2);

        GeneratedFileManager.deleteFilesByExtension(testDirectory, ".java");

        assertThat(file1).exists();
        assertThat(file2).exists();
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deleteFilesByExtension_withEmptyOrBlankExtension_shouldThrowIllegalArgumentException(String extension) {
        assertThatThrownBy(() -> GeneratedFileManager.deleteFilesByExtension(testDirectory, extension))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("extension must not be blank");
    }

    @Test
    void deleteFilesByExtension_withNullDirectory_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> GeneratedFileManager.deleteFilesByExtension(null, ".java"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("directory must not be null");
    }

    @Test
    void deleteFilesByExtension_withNullExtension_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> GeneratedFileManager.deleteFilesByExtension(testDirectory, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("extension must not be null");
    }

    @Test
    void deleteFilesByExtension_withFileInsteadOfDirectory_shouldThrowIllegalArgumentException() throws IOException {
        Path file = testDirectory.resolve("not-a-directory.txt");
        Files.createFile(file);

        assertThatThrownBy(() -> GeneratedFileManager.deleteFilesByExtension(file, ".java"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("path is not a directory");
    }

    // Helper methods

    private Collection<GeneratedFile> createMockGeneratedFiles(int count) {
        List<GeneratedFile> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            files.add(createMockGeneratedFile("File" + i + ".java"));
        }
        return files;
    }

    private GeneratedFile createMockGeneratedFile(String path) {
        return new GeneratedFile(
                GeneratedFileType.SOURCE,
                path,
                "// Generated content".getBytes());
    }

    private static Stream<Arguments> provideFileTypeTestCases() {
        return Stream.of(
                // Test case: files to create, expected files
                Arguments.of(
                        List.of("Test.java", "Main.java", "Config.json"),
                        List.of("Test.java", "Main.java", "Config.json")),
                Arguments.of(
                        List.of("Model.class", "Service.class"),
                        List.of("Model.class", "Service.class")),
                Arguments.of(
                        List.of("schema.proto", "message.proto"),
                        List.of("schema.proto", "message.proto")),
                Arguments.of(
                        List.of("data.json", "config.xml", "rules.drl"),
                        List.of("data.json", "config.xml", "rules.drl")),
                Arguments.of(
                        List.of("app.properties", "settings.yaml", "metadata.txt"),
                        List.of("app.properties", "settings.yaml", "metadata.txt")),
                Arguments.of(
                        List.of("mixed.java", "data.json", "schema.proto", "binary.class"),
                        List.of("mixed.java", "data.json", "schema.proto", "binary.class")));
    }

    private static Stream<Arguments> provideExtensionTestCases() {
        return Stream.of(
                // Test case: extension, files to create, files expected to be deleted
                Arguments.of(
                        ".java",
                        List.of("Test.java", "Main.java", "Config.xml"),
                        List.of("Test.java", "Main.java")),
                Arguments.of(
                        ".drl",
                        List.of("rules.drl", "test.DRL", "other.txt"),
                        List.of("rules.drl", "test.DRL")),
                Arguments.of(
                        "dmn",
                        List.of("decision.dmn", "process.bpmn", "model.DMN"),
                        List.of("decision.dmn", "model.DMN")),
                Arguments.of(
                        ".txt",
                        List.of("readme.txt", "notes.TXT", "data.json"),
                        List.of("readme.txt", "notes.TXT")));
    }
}
