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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFilesProviderImplTest {

    private SourceFilesProviderImpl sourceFilesProvider;

    private static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return Files.readString(path);
    }

    private String getTestFileContentByFilename(String fileName) throws URISyntaxException, IOException {
        return readFileContent("META-INF/resources/sources/" + fileName);
    }

    @BeforeEach
    public void setup() {
        sourceFilesProvider = new SourceFilesProviderImpl();
    }

    @Test
    void addSourceFile() {
        sourceFilesProvider.addSourceFile("a_process", createEmptySourceFile("myworkflow.sw.json"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .contains(createEmptySourceFile("myworkflow.sw.json"));
    }

    @Test
    void getSourceFilesByProcessId() {
        sourceFilesProvider.addSourceFile("a_process", createEmptySourceFile("myworkflow.sw.json"));
        sourceFilesProvider.addSourceFile("a_process", createEmptySourceFile("myworkflow.sw.yaml"));

        sourceFilesProvider.addSourceFile("another_process", createEmptySourceFile("myanotherworkflow.sw.json"));
        sourceFilesProvider.addSourceFile("another_process", createEmptySourceFile("myanotherworkflow.sw.yaml"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .containsExactlyInAnyOrder(
                        createEmptySourceFile("myworkflow.sw.json"),
                        createEmptySourceFile("myworkflow.sw.yaml"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("another_process"))
                .containsExactlyInAnyOrder(
                        createEmptySourceFile("myanotherworkflow.sw.json"),
                        createEmptySourceFile("myanotherworkflow.sw.yaml"));
    }

    @Test
    void getSourceFilesByProcessIdShouldNotReturnNull() {
        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .isEmpty();
    }

    @Test
    void getValidSourceFileDefinitionByProcessIdTest() throws Exception {
        sourceFilesProvider.addSourceFile("petstore_json_process", readSourceFile("petstore.json"));
        sourceFilesProvider.addSourceFile("petstore_sw_json_process", readSourceFile("petstore.sw.json"));
        sourceFilesProvider.addSourceFile("ymlgreet.sw_process", readSourceFile("ymlgreet.sw.yml"));
        sourceFilesProvider.addSourceFile("bpmn_process", readSourceFile("hiring.bpmn"));

        assertThat(sourceFilesProvider.getProcessSourceFile("petstore_sw_json_process")).contains(getTestFileContentByFilename("petstore.sw.json"));
        assertThat(sourceFilesProvider.getProcessSourceFile("ymlgreet.sw_process")).contains(getTestFileContentByFilename("ymlgreet.sw.yml"));
        assertThat(sourceFilesProvider.getProcessSourceFile("bpmn_process")).contains(getTestFileContentByFilename("hiring.bpmn"));
    }

    @Test
    void getInvalidSourceFileDefinitionByProcessIdTest() throws URISyntaxException, IOException {
        sourceFilesProvider.addSourceFile("petstore_json_process", readSourceFile("petstore.json"));

        //invalid extension
        assertThat(sourceFilesProvider.getProcessSourceFile("petstore_json_process")).isEmpty();
        //invalid process
        assertThat(sourceFilesProvider.getProcessSourceFile("invalidProcess")).isEmpty();
    }

    private SourceFile createEmptySourceFile(String uri) {
        return new SourceFile(uri, "");
    }

    private SourceFile readSourceFile(String uri) throws URISyntaxException, IOException {
        return new SourceFile(uri, getTestFileContentByFilename(uri));
    }
}
