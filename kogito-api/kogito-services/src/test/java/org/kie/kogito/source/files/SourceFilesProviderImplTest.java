/*
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
package org.kie.kogito.source.files;

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
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.bpmn"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .contains(new SourceFile("myworkflow.bpmn"));
    }

    @Test
    void getSourceFilesByProcessId() {
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.bpmn"));
        sourceFilesProvider.addSourceFile("a_process", new SourceFile("myworkflow.bpmn2"));

        sourceFilesProvider.addSourceFile("another_process", new SourceFile("myanotherworkflow.bpmn"));
        sourceFilesProvider.addSourceFile("another_process", new SourceFile("myanotherworkflow.bpmn2"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .containsExactlyInAnyOrder(
                        new SourceFile("myworkflow.bpmn"),
                        new SourceFile("myworkflow.bpmn2"));

        assertThat(sourceFilesProvider.getProcessSourceFiles("another_process"))
                .containsExactlyInAnyOrder(
                        new SourceFile("myanotherworkflow.bpmn"),
                        new SourceFile("myanotherworkflow.bpmn2"));
    }

    @Test
    void getSourceFilesByProcessIdShouldNotReturnNull() {
        assertThat(sourceFilesProvider.getProcessSourceFiles("a_process"))
                .isEmpty();
    }

    @Test
    void getValidSourceFileDefinitionByProcessIdTest() {
        SourceFile hiringBpmn = new SourceFile("hiring.bpmn");

        sourceFilesProvider.addSourceFile("bpmn_process", hiringBpmn);

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
