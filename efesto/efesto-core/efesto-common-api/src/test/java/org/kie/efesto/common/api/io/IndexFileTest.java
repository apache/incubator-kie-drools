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

import org.drools.util.PortablePath;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexFileTest {

    @Test
    void validatePathName() {
        assertThat(IndexFile.validatePathName(PortablePath.of("/this/is/valid/file.model_json")))
                .isEqualTo(PortablePath.of("/this/is/valid/file.model_json"));
    }

    @Test
    void validateWrongPathName() {
        final List<PortablePath> toValidate = Arrays.asList(PortablePath.of("/this/is/invalid/file._json"),
                PortablePath.of("/this/is/invalid/file.model"));
        toValidate.forEach(toVal -> {
            String fileName = toVal.getFileName();
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
        PortablePath source = PortablePath.of(fileName);
        assertThat(IndexFile.getModel(source)).isEqualTo(expected);
        source = PortablePath.of("/dir/" + fileName);
        assertThat(IndexFile.getModel(source)).isEqualTo(expected);
    }

    @Test
    void testGetModel() {
        String fileName = PortablePath.of("/this/is/valid/file.model_json").getFileName();
        String expected = "model";
        IndexFile indexFile = new IndexFile(fileName);
        assertThat(indexFile.getModel()).isEqualTo(expected);
    }
}