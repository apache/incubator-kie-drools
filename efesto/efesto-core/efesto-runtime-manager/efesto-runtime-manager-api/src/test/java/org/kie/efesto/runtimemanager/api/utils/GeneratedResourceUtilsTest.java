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
package org.kie.efesto.runtimemanager.api.utils;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedResourceUtilsTest {

    @Test
    void getGeneratedExecutableResource() {
        FRI fri = new FRI("testmod", "test");
        Optional<GeneratedExecutableResource> retrieved = GeneratedResourceUtils.getGeneratedExecutableResource(fri, "test");
        assertThat(retrieved).isNotNull().isPresent();
        fri = new FRI("notestmod", "test");
        retrieved = GeneratedResourceUtils.getGeneratedExecutableResource(fri, "test");
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void getGeneratedRedirectResource() {
        FRI fri = new FRI("redirecttestmod", "test");
        Optional<GeneratedRedirectResource> retrieved = GeneratedResourceUtils.getGeneratedRedirectResource(fri, "test");
        assertThat(retrieved).isNotNull().isPresent();
        fri = new FRI("redirectnotestmod", "test");
        retrieved = GeneratedResourceUtils.getGeneratedRedirectResource(fri, "test");
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void getIndexFile() {
        Optional<IndexFile> retrieved = GeneratedResourceUtils.getIndexFile("test");
        assertThat(retrieved).isNotNull().isPresent();
    }
}