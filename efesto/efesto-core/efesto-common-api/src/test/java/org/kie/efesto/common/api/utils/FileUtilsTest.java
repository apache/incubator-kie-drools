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
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class FileUtilsTest {

    private final static String TEST_FILE = "TestingEmptyFile.txt";
    private final static String NOT_EXISTING_FILE = "NotExistingFile.txt";

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
    void getFileFromFileNameExisting() {
        Optional<File> retrieved = FileUtils.getFileFromFileName(TEST_FILE);
        assertThat(retrieved).isNotNull().isNotEmpty();
    }

    @Test
    void getFileFromFileNameNotExisting() {
        Optional<File> retrieved = FileUtils.getFileFromFileName(NOT_EXISTING_FILE);
        assertThat(retrieved).isNotNull().isEmpty();
    }
}