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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileByFilePath;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileNameOrFilePath;

class IndexFileTest {


    private static IndexFile testingFile;
    @BeforeAll
    public static void setup() {
        testingFile = getFileFromFileNameOrFilePath("IndexFile.test_json", "./IndexFile.test_json")
                .map(IndexFile::new)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve IndexFile.test_json"));
    }

    @Test
    void validatePathName() {
        String toValidate = String.format("%1$sthis%1$sis%1$svalid%1$sfile.model_json", File.separator);
        assertThat(IndexFile.validatePathName(toValidate)).isEqualTo(toValidate);
    }

    @Test
    void validateWrongPathName() {
        String toValidate1 = String.format("%1$sthis%1$sis%1$sinvalid%1$sfile._json", File.separator);
        String toValidate2 = String.format("%1$sthis%1$sis%1$sinvalid%1$sfile.model", File.separator);
        final List<String> toValidate = Arrays.asList(toValidate1, toValidate2);
        toValidate.forEach(toVal -> {
            String fileName = toVal.substring(toVal.lastIndexOf(File.separator) + 1);
            String expectedMessage = String.format("Wrong file name %s", fileName);
            assertThatThrownBy(() -> IndexFile.validatePathName(toVal))
                    .isInstanceOf(KieEfestoCommonException.class)
                    .hasMessage(expectedMessage);
        });
    }

    @Test
    void getModel() {
        String fileName = "file_name.model_json";
        String expected = "model";
        String source = fileName;
        assertThat(IndexFile.getModel(source)).isEqualTo(expected);
        source = File.separator + "dir" + File.separator + fileName;
        assertThat(IndexFile.getModel(source)).isEqualTo(expected);
    }

    @Test
    void testGetModel() {
        String fileName = String.format("%1$sthis%1$sis%1$svalid%1$sfile.model_json", File.separator);
        String expected = "model";
        IndexFile indexFile = new IndexFile(fileName);
        assertThat(indexFile.getModel()).isEqualTo(expected);
    }

    @Test
    void isEqualExisting() {
        IndexFile compareFile = getFileByFilePath("./target/test-classes/IndexFile.test_json")
                .map(IndexFile::new)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve IndexFile.test_json"));
        assertThat(compareFile.getName()).isEqualTo(testingFile.getName());
        assertThat(compareFile.getPath()).isNotEqualTo(testingFile.getPath());
        assertThat(compareFile.getAbsolutePath()).isNotEqualTo(testingFile.getAbsolutePath());
        assertThat(compareFile.equals(testingFile)).isTrue();
    }

    @Test
    void isEqualNotExisting() {
        String expected = "model";
        IndexFile indexFile = new IndexFile("not/exist/", expected);
        IndexFile compareFile = new IndexFile("not/exist/", expected);
        assertThat(compareFile.getName()).isEqualTo(indexFile.getName());
        assertThat(compareFile.getPath()).isEqualTo(indexFile.getPath());
        assertThat(compareFile.getAbsolutePath()).isEqualTo(indexFile.getAbsolutePath());
        assertThat(compareFile.equals(indexFile)).isTrue();
    }

    @Test
    void isEqualMixed() {
        IndexFile compareFile = new IndexFile("not/exist/", "test");
        assertThat(compareFile.getName()).isEqualTo(testingFile.getName());
        assertThat(compareFile.equals(testingFile)).isFalse();
    }
}