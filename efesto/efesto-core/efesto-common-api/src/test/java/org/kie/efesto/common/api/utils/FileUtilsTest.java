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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.efesto.common.api.utils.FileUtils.getFile;
import static org.kie.efesto.common.api.utils.FileUtils.getFileContent;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromJar;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromResource;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromURL;
import static org.kie.efesto.common.api.utils.FileUtils.getFileInputStream;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileNameOrFilePath;
import static org.kie.efesto.common.api.utils.FileUtils.getInputStreamFromFileName;
import static org.kie.efesto.common.api.utils.FileUtils.getOptionalFileFromJar;
import static org.kie.efesto.common.api.utils.FileUtils.getOptionalFileFromResource;
import static org.kie.efesto.common.api.utils.FileUtils.getOptionalFileFromURLFile;

class FileUtilsTest {

    private final static String EMPTY_TEST_FILE = "TestingEmptyFile.txt";
    private final static String NOT_EMPTY_TEST_FILE = "IndexFile.test_json";
    private final static String NOT_EXISTING_FILE = "NotExistingFile.txt";

    private static String content;

    @BeforeAll
    public static void setup() throws IOException {
        File testingFile = getFile(NOT_EMPTY_TEST_FILE);
        assertThat(testingFile).isNotNull().exists();
        
        content = new String(Files.readAllBytes(testingFile.toPath()));
        assertThat(content).isNotNull().isNotEmpty();
    }

    @Test
    void getFile_existing() {
        assertThat(getFile(EMPTY_TEST_FILE)).isNotNull().exists();
    }

    @Test
    void getFile_nonExisting() {
        assertThatExceptionOfType(KieEfestoCommonException.class).isThrownBy(()-> getFile(NOT_EXISTING_FILE));
    }

    @Test
    void getFileInputStream_existing() throws IOException {
        assertThat(getFileInputStream(EMPTY_TEST_FILE)).isNotNull();
    }

    @Test
    void getFileInputStreamNotExisting_nonExisting() {
        assertThatExceptionOfType(KieEfestoCommonException.class).isThrownBy(()-> getFileInputStream(NOT_EXISTING_FILE));
    }

    @Test
    void getFileContent_existingNonEmpty() throws IOException {
        assertThat(getFileContent(NOT_EMPTY_TEST_FILE)).isNotNull().isNotEmpty();
    }

    @Test
    void getInputStreamFromFileName_existing() {
        assertThat(getInputStreamFromFileName(EMPTY_TEST_FILE)).isNotNull();
    }

    @Test
    void getInputStreamFromFileName_nonExisting() {
        assertThatExceptionOfType(KieEfestoCommonException.class).isThrownBy(()-> getInputStreamFromFileName(NOT_EXISTING_FILE));
    }

    @Test
    void getFileFromFileNameOrFilePath_existingInBothFileSystemAndClassLoader() {
        Optional<File> retrieved = getFileFromFileNameOrFilePath(EMPTY_TEST_FILE, EMPTY_TEST_FILE);

        assertThat(retrieved).isNotNull().isNotEmpty();
    }
    
    @Test
    void getFileFromFileNameOrFilePathExisting_existingInFileSystem() {
        String path = String.format("target%1$stest-classes%1$s%2$s", File.separator, EMPTY_TEST_FILE);
        Optional<File> retrieved = getFileFromFileNameOrFilePath(NOT_EXISTING_FILE, path);

        assertThat(retrieved).isNotNull().isNotEmpty();
    }
    
    @Test
    void getFileFromFileNameOrFilePathExisting_existingInClassLoader() {
        String path = String.format("target%1$stest-classes%1$s%2$s", File.separator, EMPTY_TEST_FILE);
        Optional<File> retrieved = getFileFromFileNameOrFilePath(path, NOT_EXISTING_FILE);

        assertThat(retrieved).isNotNull().isNotEmpty();
    }

    @Test
    void getFileFromFileNameOrFilePath_nonExisting() {
        Optional<File> retrieved = getFileFromFileNameOrFilePath(NOT_EXISTING_FILE, NOT_EXISTING_FILE);
        
        assertThat(retrieved).isNotNull().isEmpty();
    }

    @Test
    void getFileFromURL_urlIsAJar() throws IOException {
        URL url = getJarUrl();
        Optional<File> retrieved = getFileFromURL(url);

        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(MemoryFile.class);
        assertThat(retrieved.get()).canRead();
    }
    

    @Test
    void getFileFromURL_urlIsAResource() throws IOException {
        URL url = getResourceUrl();
        Optional<File> retrieved = getFileFromURL(url);

        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(File.class);
        assertThat(retrieved.get()).canRead();
    }
    

    @Test
    void getOptionalFileFromJar_existing() throws IOException {
        URL jarUrl = getJarUrl();
        
        Optional<File> retrieved = getOptionalFileFromJar(jarUrl);

        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved).get().isInstanceOf(MemoryFile.class);
    }

    @Test
    void getOptionalFileFromResource_fileIsPresent() {
        URL resourceUrl = getResourceUrl();
        Optional<File> retrieved = getOptionalFileFromResource(resourceUrl);

        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved).get().isInstanceOf(MemoryFile.class);
    }

    @Test
    void getOptionalFileFromURLFile_existing() {
        URL resourceUrl = getResourceUrl();
        Optional<File> retrieved = getOptionalFileFromURLFile(resourceUrl);

        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved).get().isInstanceOf(File.class);
    }

    @Test
    void getFileFromResource_existing() throws IOException {
        URL resourceUrl = getResourceUrl();
        File retrieved = getFileFromResource(resourceUrl);
        
        assertThat(retrieved).isNotNull().isInstanceOf(MemoryFile.class).canRead();
    }

    @Test
    void getFileFromJar_existing() throws URISyntaxException, IOException {
        URL jarUrl = getJarUrl();
        File retrieved = getFileFromJar(jarUrl);
        
        assertThat(retrieved).isNotNull().isInstanceOf(MemoryFile.class).canRead();
    }

    private static URL getResourceUrl() {
        URL toReturn = Thread.currentThread().getContextClassLoader().getResource(NOT_EMPTY_TEST_FILE);
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