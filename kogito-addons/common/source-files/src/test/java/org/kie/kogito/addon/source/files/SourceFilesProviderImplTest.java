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
package org.kie.kogito.addon.source.files;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFilesProviderImplTest {

    @Test
    void addSourceFile() {
        SourceFilesProviderImpl sourceFilesProvider = new SourceFilesProviderImpl();
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.sw.json"));

        assertThat(sourceFilesProvider.getSourceFiles("a_process"))
                .contains(new SourceFile("myworkflow.sw.json"));
    }

    @Test
    void getSourceFilesByProcessId() {
        SourceFilesProviderImpl sourceFilesProvider = new SourceFilesProviderImpl();
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.sw.json"));
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.sw.yaml"));

        sourceFilesProvider.addSourceFile("another_process", new SourceFile("myanotherworkflow.sw.json"));
        sourceFilesProvider.addSourceFile("another_process", new SourceFile("myanotherworkflow.sw.yaml"));

        assertThat(sourceFilesProvider.getSourceFiles("a_process"))
                .containsExactlyInAnyOrder(
                        new SourceFile("myworkflow.sw.json"),
                        new SourceFile("myworkflow.sw.yaml"));

        assertThat(sourceFilesProvider.getSourceFiles("another_process"))
                .containsExactlyInAnyOrder(
                        new SourceFile("myanotherworkflow.sw.json"),
                        new SourceFile("myanotherworkflow.sw.yaml"));
    }

    @Test
    void getSourceFilesByProcessIdShouldNotReturnNull() {
        SourceFilesProviderImpl sourceFilesProvider = new SourceFilesProviderImpl();

        assertThat(sourceFilesProvider.getSourceFiles("a_process"))
                .isEmpty();
    }
}