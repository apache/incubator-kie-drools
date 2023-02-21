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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.svg.service.QuarkusProcessSvgService;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProcessSvgResourceTest {

    private final static String PROCESS_INSTANCE_ID = "piId";
    private final static String PROCESS_ID = "travels";

    private ProcessSvgResource processSvgResourceTest;
    private QuarkusProcessSvgService processSvgServiceMock;

    @BeforeEach
    public void setup() {
        processSvgResourceTest = new ProcessSvgResource();
        processSvgServiceMock = mock(QuarkusProcessSvgService.class);
        processSvgResourceTest.setProcessSvgService(processSvgServiceMock);
    }

    @Test
    void getProcessSvgTest() {
        processSvgResourceTest.getProcessSvg(PROCESS_ID);
        verify(processSvgServiceMock).getProcessSvg(PROCESS_ID);
    }

    @Test
    void getExecutionPathByProcessInstanceIdTest() {
        String authHeader = "Bearer: token";
        processSvgResourceTest.getExecutionPathByProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, authHeader);
        verify(processSvgServiceMock).getProcessInstanceSvg(PROCESS_ID, PROCESS_INSTANCE_ID, authHeader);
    }

    @Test
    public void testGetTokenWithSecurityIdentity() {
        String token = "testToken";
        TokenCredential tokenCredential = new TokenCredential(token, "Bearer");
        SecurityIdentity identity = mock(SecurityIdentity.class);
        lenient().when(identity.getCredential(TokenCredential.class)).thenReturn(tokenCredential);
        processSvgResourceTest.identity = identity;
        assertThat(processSvgResourceTest.getAuthHeader("")).isEqualTo("Bearer " + token);
    }

    @Test
    public void testGetTokenWithoutSecurityIdentity() {
        assertThat(processSvgResourceTest.getAuthHeader("")).isEmpty();
    }
}