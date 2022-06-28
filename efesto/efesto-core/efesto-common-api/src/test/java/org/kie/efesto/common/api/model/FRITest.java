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
package org.kie.efesto.common.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.model.FRI.SLASH;

class FRITest {

    private final static String basePath = "base/Path";
    private final static String model = "model";


    @Test
    void getBasePath() {
        FRI retrieved = new FRI(basePath, model);
        assertThat(retrieved.getBasePath()).isEqualTo(SLASH + basePath);
    }

    @Test
    void getModel() {
        FRI retrieved = new FRI(basePath, model);
        assertThat(retrieved.getModel()).isEqualTo(model);
    }

    @Test
    void getFri() {
        FRI retrieved = new FRI(basePath, model);
        String expected = SLASH + model + SLASH + basePath;
        assertThat(retrieved.getFri()).isEqualTo(expected);
    }

    @Test
    void generateBasePath() {
        String expected = SLASH + basePath;
        String retrieved = FRI.generateBasePath(basePath, model);
        assertThat(retrieved).isEqualTo(expected);
        retrieved = FRI.generateBasePath(SLASH + basePath, model);
        assertThat(retrieved).isEqualTo(expected);
        retrieved = FRI.generateBasePath(SLASH + model + SLASH + basePath, model);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void generateFri() {
        String expected = SLASH + model + SLASH + basePath;
        String retrieved = FRI.generateFri(basePath, model);
        assertThat(retrieved).isEqualTo(expected);
        retrieved = FRI.generateFri(SLASH + basePath, model);
        assertThat(retrieved).isEqualTo(expected);
        retrieved = FRI.generateFri(SLASH + model + SLASH + basePath, model);
        assertThat(retrieved).isEqualTo(expected);
        expected = SLASH + model + SLASH + basePath + SLASH + "notmodel";
        retrieved = FRI.generateFri(basePath + SLASH + "notmodel", model);
        assertThat(retrieved).isEqualTo(expected);
    }
}