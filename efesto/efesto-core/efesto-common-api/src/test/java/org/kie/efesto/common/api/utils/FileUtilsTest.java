/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.common.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.io.MemoryFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.efesto.common.api.utils.FileUtils.getFile;

class FileUtilsTest {

    private final static String TEST_FILE = "TestingEmptyFile.txt";
    private final static String NOT_EXISTING_FILE = "NotExistingFile.txt";

    private final static String NOT_EMPTY_FILE = "IndexFile.test_json";

    private static String content;

    @BeforeAll
    public static void setup() throws IOException {
        File testingFile = getFile(NOT_EMPTY_FILE);
        assertThat(testingFile).isNotNull();
        assertThat(testingFile).exists();
        content = new String(Files.readAllBytes(testingFile.toPath()));
        assertThat(content).isNotNull().isNotEmpty();
    }

    @Test
    void getFileFromFileNameExisting() {
        File retrieved = FileUtils.getFile(TEST_FILE);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).exists();
    }

    @Test
    void getFileFromFileNameNotExisting() {
        try {
            FileUtils.getFile(NOT_EXISTING_FILE);
            fail("Expecting KieEfestoCommonException");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(KieEfestoCommonException.class);
        }
    }

    @Test
    void getFileInputStreamExisting() throws IOException {
        InputStream retrieved = FileUtils.getFileInputStream(TEST_FILE);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getFileInputStreamNotExisting() {
        try {
            FileUtils.getFileInputStream(NOT_EXISTING_FILE);
            fail("Expecting KieEfestoCommonException");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(KieEfestoCommonException.class);
        }
    }

    @Test
    void getFileContent() throws IOException {
        String retrieved = FileUtils.getFileContent(NOT_EMPTY_FILE);
        assertThat(retrieved).isNotNull().isNotEmpty();
    }

    @Test
    void getInputStreamFromFileNameExisting() {
        InputStream retrieved = FileUtils.getInputStreamFromFileName(TEST_FILE);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getInputStreamFromFileNameNotExisting() {
        try {
            FileUtils.getInputStreamFromFileName(NOT_EXISTING_FILE);
            fail("Expecting KieEfestoCommonException thrown");
        } catch (Exception e) {
            assertThat(e instanceof KieEfestoCommonException).isTrue();
        }
    }

    @Test
    void getFileFromFileNameOrFilePathExisting() {
        Optional<File> retrieved = FileUtils.getFileFromFileNameOrFilePath(TEST_FILE, TEST_FILE);
        assertThat(retrieved).isNotNull().isNotEmpty();
        String path = String.format("target%1$stest-classes%1$s%2$s", File.separator, TEST_FILE);
        retrieved = FileUtils.getFileFromFileNameOrFilePath(NOT_EXISTING_FILE, path);
        assertThat(retrieved).isNotNull().isNotEmpty();
        retrieved = FileUtils.getFileFromFileNameOrFilePath(path, NOT_EXISTING_FILE);
        assertThat(retrieved).isNotNull().isNotEmpty();
    }

    @Test
    void getFileFromFileNameOrFilePathNotExisting() {
        Optional<File> retrieved = FileUtils.getFileFromFileNameOrFilePath(NOT_EXISTING_FILE, NOT_EXISTING_FILE);
        assertThat(retrieved).isNotNull().isEmpty();
    }

    @Test
    void getFileFromURL() throws IOException {
        URL url = getJarUrl();
        assertThat(url).isNotNull();
        Optional<File> retrieved = FileUtils.getFileFromURL(url);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(MemoryFile.class);
        assertThat(retrieved.get()).canRead();

        url = getResourceUrl();
        assertThat(url).isNotNull();
        retrieved = FileUtils.getFileFromURL(url);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(File.class);
        assertThat(retrieved.get()).canRead();
    }

    @Test
    void getOptionalFileFromJar() throws IOException {
        URL jarUrl = getJarUrl();
        assertThat(jarUrl).isNotNull();
        Optional<File> retrieved = FileUtils.getOptionalFileFromJar(jarUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved).get().isInstanceOf(MemoryFile.class);
    }

    @Test
    void getOptionalFileFromResource() {
        URL resourceUrl = getResourceUrl();
        Optional<File> retrieved = FileUtils.getOptionalFileFromResource(resourceUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved).get().isInstanceOf(MemoryFile.class);
    }

    @Test
    void getOptionalFileFromURLFile() {
        URL resourceUrl = getResourceUrl();
        Optional<File> retrieved = FileUtils.getOptionalFileFromURLFile(resourceUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved).get().isInstanceOf(File.class);
    }

    @Test
    void getFileFromResource() throws IOException {
        URL resourceUrl = getResourceUrl();
        File retrieved = FileUtils.getFileFromResource(resourceUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(MemoryFile.class);
        assertThat(retrieved).canRead();
    }

    @Test
    void getFileFromJar() throws URISyntaxException, IOException {
        URL jarUrl = getJarUrl();
        assertThat(jarUrl).isNotNull();
        File retrieved = FileUtils.getFileFromJar(jarUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(MemoryFile.class);
        assertThat(retrieved).canRead();
    }

    private static URL getResourceUrl() {
        URL toReturn = Thread.currentThread().getContextClassLoader().getResource(NOT_EMPTY_FILE);
        assertThat(toReturn).isNotNull();
        return toReturn;
    }

    private static URL getJarUrl() throws MalformedURLException {
        URL retrieved = Thread.currentThread().getContextClassLoader().getResource("TestJar.jar");
        assertThat(retrieved).isNotNull();
        String newString = "jar:" + retrieved + "!/IndexFile.testb_json";
        return new URL(newString);
    }
}