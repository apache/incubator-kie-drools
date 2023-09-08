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
package org.kie.efesto.common.api.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileNameOrFilePath;

class MemoryFileTest {

    private static final String FILE_NAME = "IndexFile.test_json";
    private static final String FILE_PATH = "./" + FILE_NAME;
    private static File testingFile;
    private static byte[] content;

    @BeforeAll
    public static void setup() throws IOException {
        testingFile = getFileFromFileNameOrFilePath(FILE_NAME, FILE_PATH)
                .map(IndexFile::new)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve " + FILE_NAME));
        assertThat(testingFile).exists();
        assertThat(testingFile).canRead();
        content = Files.readAllBytes(testingFile.toPath());
        assertThat(content).isNotNull().isNotEmpty();
    }

    @Test
    void instantiateFromPath() throws IOException {
        MemoryFile retrieved = new MemoryFile(testingFile.toPath());
        commonVerifyMemoryFile(retrieved);
    }

    @Test
    void instantiateFromURL() throws IOException {
        MemoryFile retrieved = new MemoryFile(testingFile.toURI().toURL());
        commonVerifyMemoryFile(retrieved);
    }

    private void commonVerifyMemoryFile(MemoryFile toVerify) {
        assertThat(toVerify).exists();
        assertThat(toVerify).canRead();
        assertThat(toVerify.canWrite()).isFalse();
        assertThat(toVerify.getName()).isEqualTo(FILE_NAME);
        assertThat(toVerify.length()).isEqualTo(content.length);
        assertThat(toVerify.getContent()).isEqualTo(content);
    }
}