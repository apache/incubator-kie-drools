/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.svg.rest;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.svg.service.SpringBootProcessSvgService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SpringBootProcessSvgResourceTest {

    private final static String PROCESS_INSTANCE_ID = "piId";
    private final static String PROCESS_ID = "travels";
    private final static String AUTH_HEADER = "Bearer: token";

    private SpringBootProcessSvgResource processSvgResourceTest;
    private SpringBootProcessSvgService processSvgServiceMock;

    @BeforeEach
    public void setup() {
        processSvgResourceTest = new SpringBootProcessSvgResource();
        processSvgServiceMock = mock(SpringBootProcessSvgService.class);
        processSvgResourceTest.setProcessSvgService(processSvgServiceMock);
    }

    @Test
    void getProcessSvgTest() {
        processSvgResourceTest.getProcessSvg(PROCESS_ID);
        verify(processSvgServiceMock).getProcessSvg(PROCESS_ID);
    }

    @Test
    void getExecutionPathByProcessInstanceIdTest() throws IOException {
        processSvgResourceTest.getExecutionPathByProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, AUTH_HEADER);
        verify(processSvgServiceMock).getProcessInstanceSvg(PROCESS_ID, PROCESS_INSTANCE_ID, AUTH_HEADER);
    }
}