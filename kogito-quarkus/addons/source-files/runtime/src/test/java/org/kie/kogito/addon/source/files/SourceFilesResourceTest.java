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
package org.kie.kogito.addon.source.files;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class SourceFilesResourceTest {
    private static final String PROCESS_ID = "processId";

    private SourceFilesResource sourceFilesTestResource;

    @Mock
    private SourceFilesProvider mockSourceFileProvider;

    @BeforeEach
    void setup() {
        sourceFilesTestResource = new SourceFilesResource();
        mockSourceFileProvider = mock(SourceFilesProvider.class);
        sourceFilesTestResource.setSourceFilesProvider(mockSourceFileProvider);
    }

    @Test
    void getSourceFilesByProcessIdTest() {
        sourceFilesTestResource.getSourceFilesByProcessId(PROCESS_ID);
        verify(mockSourceFileProvider).getProcessSourceFiles(PROCESS_ID);
    }

    @Test
    void getEmptySourceFileByProcessIdTest() {
        when(mockSourceFileProvider.getProcessSourceFile(PROCESS_ID)).thenReturn(Optional.empty());
        assertThat(sourceFilesTestResource.getSourceFileByProcessId(PROCESS_ID).getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(mockSourceFileProvider).getProcessSourceFile(PROCESS_ID);
    }

    @Test
    void getValidSourceFileByProcessIdTest() {
        when(mockSourceFileProvider.getProcessSourceFile(PROCESS_ID)).thenReturn(Optional.of(new SourceFile("petstore.sw.json")));
        assertThat(sourceFilesTestResource.getSourceFileByProcessId(PROCESS_ID).getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(mockSourceFileProvider).getProcessSourceFile(PROCESS_ID);
    }
}
