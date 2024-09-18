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
import org.kie.kogito.internal.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.internal.process.workitem.InvalidTransitionException;
import org.kie.kogito.internal.process.workitem.NotAuthorizedException;
import org.kie.kogito.internal.process.workitem.WorkItemExecutionException;
import org.kie.kogito.internal.process.workitem.WorkItemNotFoundException;
import org.kie.kogito.process.NodeInstanceNotFoundException;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.VariableViolationException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class BaseExceptionHandlerTest {

    private BaseExceptionsHandler tested;

    @Mock
    private Object badRequestResponse;

    @Mock
    private Object conflictResponse;

    @Mock
    private Object internalErrorResponse;

    @Mock
    private Object notFoundResponse;

    @Mock
    private Object forbiddenResponse;

    @BeforeEach
    void setUp() {
        tested = spy(new BaseExceptionsHandler() {
            @Override
            protected Object badRequest(Object body) {
                return badRequestResponse;
            }

            @Override
            protected Object conflict(Object body) {
                return conflictResponse;
            }

            @Override
            protected Object internalError(Object body) {
                return internalErrorResponse;
            }

            @Override
            protected Object notFound(Object body) {
                return notFoundResponse;
            }

            @Override
            protected Object forbidden(Object body) {
                return forbiddenResponse;
            }
        });
    }

    @Test
    void testMapInvalidLifeCyclePhaseException() {
        Object response = tested.mapException(new InvalidLifeCyclePhaseException("message"));
        assertThat(response).isEqualTo(badRequestResponse);
    }

    @Test
    void testMapInvalidTransitionException() {
        Object response = tested.mapException(new InvalidTransitionException("message"));
        assertThat(response).isEqualTo(badRequestResponse);
    }

    @Test
    void testMapNodeInstanceNotFoundException() {
        Object response = tested.mapException(new NodeInstanceNotFoundException("processInstanceId", "nodeInstanceId"));
        assertThat(response).isEqualTo(notFoundResponse);
    }

    @Test
    void testMapNotAuthorizedException() {
        Object response = tested.mapException(new NotAuthorizedException("message"));
        assertThat(response).isEqualTo(forbiddenResponse);
    }

    @Test
    void testMapProcessInstanceDuplicatedException() {
        Object response = tested.mapException(new ProcessInstanceDuplicatedException("processInstanceId"));
        assertThat(response).isEqualTo(conflictResponse);
    }

    @Test
    void testMapProcessInstanceExecutionException() {
        Object response = tested.mapException(new ProcessInstanceExecutionException("processInstanceId", "nodeId", "message"));
        assertThat(response).isEqualTo(internalErrorResponse);
    }

    @Test
    void testMapProcessInstanceNotFoundException() {
        Object response = tested.mapException(new ProcessInstanceNotFoundException("processInstanceId"));
        assertThat(response).isEqualTo(notFoundResponse);
    }

    @Test
    void testMapWorkItemNotFoundException() {
        Object response = tested.mapException(new WorkItemNotFoundException("workItemId"));
        assertThat(response).isEqualTo(notFoundResponse);
    }

    @Test
    void testMapVariableViolationException() {
        Object response = tested.mapException(new VariableViolationException("processInstanceId", "variable",
                "message"));
        assertThat(response).isEqualTo(badRequestResponse);
    }

    @Test
    void testMapWorkItemExecutionException() {
        assertThat(tested.mapException(new WorkItemExecutionException("400", "message"))).isEqualTo(badRequestResponse);
        assertThat(tested.mapException(new WorkItemExecutionException("404", "message"))).isEqualTo(notFoundResponse);
        assertThat(tested.mapException(new WorkItemExecutionException("403", "message"))).isEqualTo(forbiddenResponse);
        assertThat(tested.mapException(new WorkItemExecutionException("409", "message"))).isEqualTo(conflictResponse);
        assertThat(tested.mapException(new WorkItemExecutionException("500", "message"))).isEqualTo(internalErrorResponse);
        assertThat(tested.mapException(new WorkItemExecutionException("One error code"))).isEqualTo(internalErrorResponse);
    }
}
