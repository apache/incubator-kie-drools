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
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.source.files.SourceFile;
import org.kie.kogito.source.files.SourceFilesProvider;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SourceFilesRestControllerTest {

    private static final String PROCESS_ID = "processId";

    private SourceFilesRestController sourceFilesRestController;

    @Mock
    private SourceFilesProvider mockSourceFileProvider;

    @BeforeEach
    void setup() {
        sourceFilesRestController = new SourceFilesRestController(mockSourceFileProvider);
    }

    @Test
    void getSourceFilesByProcessIdTest() {
        sourceFilesRestController.getSourceFilesByProcessId(PROCESS_ID);
        verify(mockSourceFileProvider).getProcessSourceFiles(PROCESS_ID);
    }

    @Test
    void getEmptySourceFileByProcessIdTest() throws Exception {
        when(mockSourceFileProvider.getProcessSourceFile(PROCESS_ID)).thenReturn(Optional.empty());
        assertThat(sourceFilesRestController.getSourceFileByProcessId(PROCESS_ID).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(mockSourceFileProvider).getProcessSourceFile(PROCESS_ID);
    }

    @Test
    void getValidSourceFileByProcessIdTest() throws Exception {
        when(mockSourceFileProvider.getProcessSourceFile(PROCESS_ID)).thenReturn(Optional.of(new SourceFile("petstore.sw.json")));
        assertThat(sourceFilesRestController.getSourceFileByProcessId(PROCESS_ID).getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mockSourceFileProvider).getProcessSourceFile(PROCESS_ID);
    }
}
