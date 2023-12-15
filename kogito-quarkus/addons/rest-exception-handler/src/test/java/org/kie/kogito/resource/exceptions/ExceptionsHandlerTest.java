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
package org.kie.kogito.resource.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionsHandlerTest {

    private ExceptionsHandler tested;

    @Mock
    private Object body;

    @Mock
    private RuntimeDelegate runtimeDelegate;

    @Mock
    private Response.ResponseBuilder builder;

    @Mock
    private Response response;

    @BeforeEach
    void setUp() {
        tested = new ExceptionsHandler();
        RuntimeDelegate.setInstance(runtimeDelegate);
        when(runtimeDelegate.createResponseBuilder()).thenReturn(builder);
        when(builder.status(any(Response.StatusType.class))).thenReturn(builder);
        when(builder.header(anyString(), any())).thenReturn(builder);
        when(builder.entity(any())).thenReturn(builder);
        when(builder.build()).thenReturn(response);
    }

    @Test
    void testBadRequest() {
        tested.badRequest(body);
        assertRequest(Response.Status.BAD_REQUEST);
    }

    private void assertRequest(Response.Status status) {
        verify(builder).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        verify(builder).status((Response.StatusType) status);
        verify(builder).entity(body);
    }

    @Test
    void testConflict() {
        tested.conflict(body);
        assertRequest(Response.Status.CONFLICT);
    }

    @Test
    void testInternalError() {
        tested.internalError(body);
        assertRequest(Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testNotFound() {
        tested.notFound(body);
        assertRequest(Response.Status.NOT_FOUND);
    }

    @Test
    void testForbidden() {
        tested.forbidden(body);
        assertRequest(Response.Status.FORBIDDEN);
    }
}
