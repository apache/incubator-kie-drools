/**
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
package org.kie.efesto.common.api.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.MemoryFile;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryFileUtilsTest {

    private final static String TEST_FILE = "TestingEmptyFile.txt";
    private final static String NOT_EXISTING_FILE = "NotExistingFile.txt";

    private final static String NOT_EMPTY_FILE = "IndexFile.test_json";



    @Test
    void getFileFromFileNameOrFilePathExisting() {
        Optional<File> retrieved = MemoryFileUtils.getFileFromFileNameOrFilePath(TEST_FILE, TEST_FILE);
        assertThat(retrieved).isNotNull().isNotEmpty();
        String path = String.format("target%1$stest-classes%1$s%2$s", File.separator, TEST_FILE);
        retrieved = MemoryFileUtils.getFileFromFileNameOrFilePath(NOT_EXISTING_FILE, path);
        assertThat(retrieved).isNotNull().isNotEmpty();
        retrieved = MemoryFileUtils.getFileFromFileNameOrFilePath(path, NOT_EXISTING_FILE);
        assertThat(retrieved).isNotNull().isNotEmpty();
    }

    @Test
    void getFileFromFileNameOrFilePathNotExisting() {
        Optional<File> retrieved = MemoryFileUtils.getFileFromFileNameOrFilePath(NOT_EXISTING_FILE, NOT_EXISTING_FILE);
        assertThat(retrieved).isNotNull().isEmpty();
    }

    @Test
    void getFileFromURL() throws IOException {
        URL url = getJarUrl();
        assertThat(url).isNotNull();
        Optional<File> retrieved = MemoryFileUtils.getFileFromURL(url);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(MemoryFile.class);
        assertThat(retrieved.get()).canRead();

        url = getResourceUrl();
        assertThat(url).isNotNull();
        retrieved = MemoryFileUtils.getFileFromURL(url);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(File.class);
        assertThat(retrieved.get()).canRead();
    }

    @Test
    void getOptionalFileFromJar() throws IOException {
        URL jarUrl = getJarUrl();
        assertThat(jarUrl).isNotNull();
        Optional<File> retrieved = MemoryFileUtils.getOptionalFileFromJar(jarUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved).get().isInstanceOf(MemoryFile.class);
    }

    @Test
    void getOptionalFileFromResource() {
        URL resourceUrl = getResourceUrl();
        Optional<File> retrieved = MemoryFileUtils.getOptionalFileFromResource(resourceUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved).get().isInstanceOf(MemoryFile.class);
    }

    @Test
    void getOptionalFileFromURLFile() {
        URL resourceUrl = getResourceUrl();
        Optional<File> retrieved = MemoryFileUtils.getOptionalFileFromURLFile(resourceUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved).get().isInstanceOf(File.class);
    }

    @Test
    void getFileFromResource() throws IOException {
        URL resourceUrl = getResourceUrl();
        File retrieved = MemoryFileUtils.getFileFromResource(resourceUrl);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(MemoryFile.class);
        assertThat(retrieved).canRead();
    }

    @Test
    void getFileFromJar() throws URISyntaxException, IOException {
        URL jarUrl = getJarUrl();
        assertThat(jarUrl).isNotNull();
        File retrieved = MemoryFileUtils.getFileFromJar(jarUrl);
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