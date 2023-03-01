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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFilesProviderImplTest {

    private SourceFilesProviderImpl sourceFilesProvider;

    @BeforeEach
    public void setup() {
        sourceFilesProvider = new SourceFilesProviderImpl();
    }

    @Test
    void addSourceFile() {
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.sw.json"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .contains(new SourceFile("myworkflow.sw.json"));
    }

    @Test
    void getSourceFilesByProcessId() {
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.sw.json"));
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.sw.yaml"));

        sourceFilesProvider.addSourceFile("another_process", new SourceFile("myanotherworkflow.sw.json"));
        sourceFilesProvider.addSourceFile("another_process", new SourceFile("myanotherworkflow.sw.yaml"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .containsExactlyInAnyOrder(
                        new SourceFile("myworkflow.sw.json"),
                        new SourceFile("myworkflow.sw.yaml"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("another_process"))
                .containsExactlyInAnyOrder(
                        new SourceFile("myanotherworkflow.sw.json"),
                        new SourceFile("myanotherworkflow.sw.yaml"));
    }

    @Test
    void getSourceFilesByProcessIdShouldNotReturnNull() {
        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .isEmpty();
    }

    @Test
    void getValidSourceFileDefinitionByProcessIdTest() {
        SourceFile petstoreJson = new SourceFile("petstore.json");
        SourceFile petstoreSwJson = new SourceFile("petstore.sw.json");
        SourceFile ymlgreetSwYml = new SourceFile("ymlgreet.sw.yml");
        SourceFile hiringBpmn = new SourceFile("hiring.bpmn");

        sourceFilesProvider.addSourceFile("petstore_json_process", petstoreJson);
        sourceFilesProvider.addSourceFile("petstore_sw_json_process", petstoreSwJson);
        sourceFilesProvider.addSourceFile("ymlgreet.sw_process", ymlgreetSwYml);
        sourceFilesProvider.addSourceFile("bpmn_process", hiringBpmn);

        assertThat(sourceFilesProvider.getProcessSourceFile("petstore_sw_json_process")).contains(petstoreSwJson);
        assertThat(sourceFilesProvider.getProcessSourceFile("ymlgreet.sw_process")).contains(ymlgreetSwYml);
        assertThat(sourceFilesProvider.getProcessSourceFile("bpmn_process")).contains(hiringBpmn);
    }

    @Test
    void getInvalidSourceFileDefinitionByProcessIdTest() {
        sourceFilesProvider.addSourceFile("petstore_json_process", new SourceFile("petstore.json"));

        //invalid extension
        assertThat(sourceFilesProvider.getProcessSourceFile("petstore_json_process")).isEmpty();
        //invalid process
        assertThat(sourceFilesProvider.getProcessSourceFile("invalidProcess")).isEmpty();
    }
}
