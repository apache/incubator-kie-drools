package org.kie.efesto.common.api.io;/*
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

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class IndexFileTest {

    @Test
    void validatePathName() {
        String toValidate = "/this/is/valid/file.model_json";
        assertThat(IndexFile.validatePathName(toValidate)).isEqualTo(toValidate);
    }

    @Test
    void validateWrongPathName() {
        String toValidate = "/this/is/invalid/file._json";
        try {
            IndexFile.validatePathName(toValidate);
            fail("Expecting KieEfestoCommonException");
        } catch (Exception e) {
            assertThat(e instanceof KieEfestoCommonException).isTrue();
            String expected = "Wrong file name file._json";
            assertThat(e.getMessage()).isEqualTo(expected);
        }
        toValidate = "/this/is/invalid/file.model";
        try {
            IndexFile.validatePathName(toValidate);
            fail("Expecting KieEfestoCommonException");
        } catch (Exception e) {
            assertThat(e instanceof KieEfestoCommonException).isTrue();
            String expected = "Wrong file name file.model";
            assertThat(e.getMessage()).isEqualTo(expected);
        }
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
        String fileName = "/this/is/valid/file.model_json";
        String expected = "model";
        IndexFile indexFile = new IndexFile(fileName);
        assertThat(indexFile.getModel()).isEqualTo(expected);
    }
}