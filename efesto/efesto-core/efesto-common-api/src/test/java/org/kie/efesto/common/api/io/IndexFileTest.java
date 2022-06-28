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
package org.kie.efesto.common.api.io;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexFileTest {

    @Test
    void validatePathName() {
        String toValidate = "/this/is/valid/file.model_json";
        assertThat(IndexFile.validatePathName(toValidate)).isEqualTo(toValidate);
    }

    @Test
    void validateWrongPathName() {
        final List<String> toValidate = Arrays.asList("/this/is/invalid/file._json", "/this/is/invalid/file.model");
        toValidate.forEach(toVal -> {
            String fileName = toVal.substring(toVal.lastIndexOf('/') + 1);
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
        String fileName = "/this/is/valid/file.model_json";
        String expected = "model";
        IndexFile indexFile = new IndexFile(fileName);
        assertThat(indexFile.getModel()).isEqualTo(expected);
    }
}